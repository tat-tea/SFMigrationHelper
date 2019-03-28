package jp.co.ncast.form;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ButtonAction {

	private static ArrayList<String> targetTypeList = new ArrayList<String>();
	private static ArrayList<String> fieldList = new ArrayList<String>();
	private ButtonActionParameter param = null;

	static {
		// 除外リスト
		targetTypeList.add("MasterDetail");

		// 除外リスト
		fieldList.add("Name");
		fieldList.add("OwnerId");
		fieldList.add("Status");
		fieldList.add("Priority");
	}

	public ButtonAction(ButtonActionParameter param) {
		this.param = param;
	}

	public void createXML() throws Exception {
		try {

			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			// Documentオブジェクトを取得
			Document xmlDocument = builder.newDocument();

			Element Profile = xmlDocument.createElement("Profile");
			Profile.setAttribute("Profile", "http://soap.sforce.com/2006/04/metadata");
			xmlDocument.appendChild(Profile);

			// XML作成
			File newXML = new File(param.getMetaFilePath() + "\\" + param.getProfileName() + ".profile");
			FileOutputStream fos = new FileOutputStream(newXML);
			StreamResult result = new StreamResult(fos);

			// Transformerファクトリを生成
			TransformerFactory transFactory = TransformerFactory.newInstance();
			// Transformerを取得
			Transformer transformer = transFactory.newTransformer();

			// エンコード：UTF-8、インデントありを指定
			transformer.setOutputProperty("encoding", "UTF-8");
			transformer.setOutputProperty("indent", "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			// アプリケーション
			if (param.isChckbx_Application()) {
				xmlDocument = editApplication(xmlDocument, Profile);
			}
			// APEXクラス
			if (param.isChckbx_Apex()) {
				xmlDocument = editApex(xmlDocument, Profile);
			}
			// カスタム項目
			if (param.isChckbx_CustomField()) {
				xmlDocument = editCustomField(xmlDocument, documentBuilder, Profile);
			}
			// APEXクラス
			if (param.isChckbx_VFPage()) {
				xmlDocument = editVF(xmlDocument, Profile);
			}
			// タブ
			if (param.isChckbx_Tab()) {
				xmlDocument = editTab(xmlDocument, Profile);
			}
			// オブジェクトのCRUD
			if (param.isChckbx_ObjectCRUD()) {
				xmlDocument = editObjectCRUD(xmlDocument, Profile);
			}
			// レコードタイプ
			if (param.isChckbx_RecordType()) {
				xmlDocument = editRecordType(xmlDocument, documentBuilder, Profile);
			}

			// ページレイアウト割り当ては手動やな。。

			// transformerに渡すソースを生成
			DOMSource source = new DOMSource(xmlDocument);

			// 出力実行
			transformer.transform(source, result);
			fos.close();

		} catch (Exception ex) {
			throw ex;
		}
	}

	private Document editCustomField(Document xmlDocument, DocumentBuilder documentBuilder, Element Profile)
			throws Exception {

		File objectDir = new File(param.getMetaFilePath() + "\\objects");
		File[] objectList = objectDir.listFiles();

		if (objectList == null || objectList.length == 0) {
			throw new Exception("objectメタデータが存在しません。");
		}

		HashMap<String, ArrayList<String>> outputMap = new HashMap<String, ArrayList<String>>();
		for (File file : objectList) {
			// 拡張子チェック
			if (!file.getName().endsWith(".object")) {
				continue;
			}
			Document document = documentBuilder.parse(file);

			Element root = document.getDocumentElement();
			NodeList objectNodeList = root.getChildNodes();

			ArrayList<String> fieldNames = new ArrayList<String>();

			// CustomObjectタグの子供
			for (int i = 0; i < objectNodeList.getLength(); i++) {
				Node personNode = objectNodeList.item(i);
				if (personNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fieldElement = (Element) personNode;

					if (fieldElement.getNodeName().equals("fields")) {

						String customField = null;
						Boolean targetFlg = true;
						Boolean targetField = false;
						Boolean required = false;

						NodeList fieldChildrenNodeList = fieldElement.getChildNodes();
						for (int j = 0; j < fieldChildrenNodeList.getLength(); j++) {
							Node node = fieldChildrenNodeList.item(j);
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element childElement = (Element) node;
								if ("fullName".equals(node.getNodeName())) {
									if (!fieldList.contains(node.getTextContent())) {
										targetField = true;
										customField = node.getTextContent();
									}
								} else if ("type".equals(node.getNodeName())
										&& targetTypeList.contains(node.getTextContent())) {
									targetFlg = false;
								} else if ("required".equals(node.getNodeName())
										&& "true".equals(node.getTextContent())) {
									// 必須の項目は項目レベルセキュリティを設定できまへん。。
									required = true;
								}
							}
						}
						// データ型がOK、必須でない場合であれば出力
						if (targetFlg && required == false && targetField) {
							fieldNames.add(customField);
						}
					}
				}
			}
			// 出力用マップにつめつめ
			outputMap.put(file.getName().substring(0, file.getName().length() - 7), fieldNames);
		}

		// カスタムオブジェクトの権限設定
		for (String objName : outputMap.keySet()) {
			for (String fieldName : outputMap.get(objName)) {

				// ルート要素を追加する
				Element fieldPermissions = xmlDocument.createElement("fieldPermissions");
				Profile.appendChild(fieldPermissions);

				// 編集可
				Element editable = xmlDocument.createElement("editable");
				editable.appendChild(xmlDocument.createTextNode("true"));
				fieldPermissions.appendChild(editable);

				// 項目名
				Element field = xmlDocument.createElement("field");
				field.appendChild(xmlDocument.createTextNode(objName + "." + fieldName));
				fieldPermissions.appendChild(field);

				// 参照可
				Element readable = xmlDocument.createElement("readable");
				readable.appendChild(xmlDocument.createTextNode("true"));
				fieldPermissions.appendChild(readable);
			}
		}
		return xmlDocument;
	}

	// アプリケーション権限設定
	private Document editApplication(Document xmlDocument, Element Profile) throws Exception {

		File apptDir = new File(param.getMetaFilePath() + "\\applications");
		File[] appList = apptDir.listFiles();

		System.out.println(param);

		if (appList == null || appList.length == 0) {
			throw new Exception("applicationメタデータが存在しません。");
		}

		for (File file : appList) {
			// 拡張子チェック
			if (!file.getName().endsWith(".app")) {
				continue;
			}

			// ルート要素を追加する
			Element appVisible = xmlDocument.createElement("applicationVisibilities");
			Profile.appendChild(appVisible);

			// アプリケーション名
			Element appName = xmlDocument.createElement("application");
			appName.appendChild(xmlDocument.createTextNode(file.getName().substring(0, file.getName().length() - 4)));
			appVisible.appendChild(appName);

			// 参照権限
			Element defaultApp = xmlDocument.createElement("default");
			defaultApp.appendChild(xmlDocument.createTextNode("false"));
			appVisible.appendChild(defaultApp);

			// 参照権限
			Element visible = xmlDocument.createElement("visible");
			visible.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(visible);
		}
		return xmlDocument;
	}

	// APEXクラス権限設定
	private Document editApex(Document xmlDocument, Element Profile) throws Exception {

		File apexDir = new File(param.getMetaFilePath() + "\\classes");
		File[] apexList = apexDir.listFiles();

		if (apexList == null || apexList.length == 0) {
			throw new Exception("APEXクラスメタデータが存在しません。");
		}

		for (File file : apexList) {
			// 拡張子チェック
			if (!file.getName().endsWith(".cls")) {
				continue;
			}

			// ルート要素を追加する
			Element appVisible = xmlDocument.createElement("classAccesses");
			Profile.appendChild(appVisible);

			// クラス名
			Element appName = xmlDocument.createElement("apexClass");
			appName.appendChild(xmlDocument.createTextNode(file.getName().substring(0, file.getName().length() - 4)));
			appVisible.appendChild(appName);

			// 参照権限
			Element visible = xmlDocument.createElement("enabled");
			visible.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(visible);
		}
		return xmlDocument;
	}

	// VisualForceページ権限設定
	private Document editVF(Document xmlDocument, Element Profile) throws Exception {

		File apexDir = new File(param.getMetaFilePath() + "\\pages");
		File[] apexList = apexDir.listFiles();

		if (apexList == null || apexList.length == 0) {
			throw new Exception("VisualForceページメタデータが存在しません。");
		}

		for (File file : apexList) {
			// 拡張子チェック
			if (!file.getName().endsWith(".page")) {
				continue;
			}

			// ルート要素を追加する
			Element appVisible = xmlDocument.createElement("pageAccesses");
			Profile.appendChild(appVisible);

			// クラス名
			Element appName = xmlDocument.createElement("apexPage");
			appName.appendChild(xmlDocument.createTextNode(file.getName().substring(0, file.getName().length() - 5)));
			appVisible.appendChild(appName);

			// 参照権限
			Element visible = xmlDocument.createElement("enabled");
			visible.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(visible);
		}
		return xmlDocument;
	}

	// CustomTab参照権限設定
	private Document editTab(Document xmlDocument, Element Profile) throws Exception {

		File apexDir = new File(param.getMetaFilePath() + "\\tabs");
		File[] apexList = apexDir.listFiles();

		if (apexList == null || apexList.length == 0) {
			throw new Exception("カスタムタブメタデータが存在しません。");
		}

		for (File file : apexList) {
			// 拡張子チェック
			if (!file.getName().endsWith(".tab")) {
				continue;
			}

			// ルート要素を追加する
			Element appVisible = xmlDocument.createElement("tabVisibilities");
			Profile.appendChild(appVisible);

			// クラス名
			Element appName = xmlDocument.createElement("tab");
			appName.appendChild(xmlDocument.createTextNode(file.getName().substring(0, file.getName().length() - 4)));
			appVisible.appendChild(appName);

			// 参照権限
			Element visible = xmlDocument.createElement("visibility");
			visible.appendChild(xmlDocument.createTextNode("DefaultOn"));
			appVisible.appendChild(visible);
		}
		return xmlDocument;
	}

	// ObjectCRUD設定
	private Document editObjectCRUD(Document xmlDocument, Element Profile) throws Exception {

		File apexDir = new File(param.getMetaFilePath() + "\\objects");
		File[] apexList = apexDir.listFiles();

		if (apexList == null || apexList.length == 0) {
			throw new Exception("カスタムオブジェクトメタデータが存在しません。");
		}

		for (File file : apexList) {
			// 拡張子チェック
			if (!file.getName().endsWith(".object")) {
				continue;
			}

			// ルート要素を追加する
			Element appVisible = xmlDocument.createElement("objectPermissions");
			Profile.appendChild(appVisible);

			// 作成
			Element create = xmlDocument.createElement("allowCreate");
			create.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(create);

			// 削除
			Element delete = xmlDocument.createElement("allowDelete");
			delete.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(delete);

			// 編集
			Element edit = xmlDocument.createElement("allowEdit");
			edit.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(edit);

			// 参照
			Element read = xmlDocument.createElement("allowRead");
			read.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(read);

			// データ管理＿すべて表示
			Element modifyAllRecords = xmlDocument.createElement("modifyAllRecords");
			modifyAllRecords.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(modifyAllRecords);

			// 対象オブジェクト
			Element object = xmlDocument.createElement("object");
			object.appendChild(xmlDocument.createTextNode(file.getName().substring(0, file.getName().length() - 7)));
			appVisible.appendChild(object);

			// データ管理＿すべて変更
			Element viewAllRecords = xmlDocument.createElement("viewAllRecords");
			viewAllRecords.appendChild(xmlDocument.createTextNode("true"));
			appVisible.appendChild(viewAllRecords);

		}
		return xmlDocument;
	}

	// レコードタイプの割り当て
	private Document editRecordType(Document xmlDocument, DocumentBuilder documentBuilder, Element Profile)
			throws Exception {

		File objectDir = new File(param.getMetaFilePath() + "\\objects");
		File[] objectList = objectDir.listFiles();

		if (objectList == null || objectList.length == 0) {
			throw new Exception("objectメタデータが存在しません。");
		}

		// オブジェクトごとにレコードタイプ名を吸い上げる。
		HashMap<String, ArrayList<String>> recordTypeMap = new HashMap<String, ArrayList<String>>();
		for (File file : objectList) {
			// 拡張子チェック
			if (!file.getName().endsWith(".object")) {
				continue;
			}
			Document document = documentBuilder.parse(file);

			Element root = document.getDocumentElement();
			NodeList objectNodeList = root.getChildNodes();

			ArrayList<String> recordTypeNameList = new ArrayList<String>();

			// CustomObjectタグの子供
			for (int i = 0; i < objectNodeList.getLength(); i++) {
				Node personNode = objectNodeList.item(i);
				if (personNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fieldElement = (Element) personNode;

					if (fieldElement.getNodeName().equals("recordTypes")) {

						NodeList fieldChildrenNodeList = fieldElement.getChildNodes();
						for (int j = 0; j < fieldChildrenNodeList.getLength(); j++) {
							Node node = fieldChildrenNodeList.item(j);
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								if ("fullName".equals(node.getNodeName())) {
									recordTypeNameList.add(node.getTextContent());
								}
							}
						}
					}
				}
			}
			// 出力用マップにつめつめ
			recordTypeMap.put(file.getName().substring(0, file.getName().length() - 7), recordTypeNameList);
		}

		// カスタムオブジェクトの権限設定
		for (String objName : recordTypeMap.keySet()) {
			// レコタイのデフォルトを１件目に指定。決め打ち。。。。
			Boolean defaultFlg = true;
			for (String recordTypeName : recordTypeMap.get(objName)) {

				// ルート要素を追加する
				Element fieldPermissions = xmlDocument.createElement("recordTypeVisibilities");
				Profile.appendChild(fieldPermissions);

				// デフォルト（１件目はデフォルトにしちゃう）
				Element editable = xmlDocument.createElement("default");
				if (defaultFlg) {
					editable.appendChild(xmlDocument.createTextNode("true"));
					defaultFlg = false;
				} else {
					editable.appendChild(xmlDocument.createTextNode("false"));
				}
				fieldPermissions.appendChild(editable);

				// レコードタイプ名
				Element field = xmlDocument.createElement("recordType");
				field.appendChild(xmlDocument.createTextNode(objName + "." + recordTypeName));
				fieldPermissions.appendChild(field);

				// 参照可
				Element readable = xmlDocument.createElement("visible");
				readable.appendChild(xmlDocument.createTextNode("true"));
				fieldPermissions.appendChild(readable);
			}
		}
		return xmlDocument;
	}

}
