package jp.co.ncast.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ProfileMetaGenerateForm extends JFrame implements ActionListener{

	JTextArea textArea;
	JCheckBox chckbx_Tab;
	JCheckBox chckbx_Apex;
	JCheckBox chckbx_VFPage;
	JCheckBox chckbx_CustomField;
	JCheckBox chckbx_Application;
	JCheckBox chckbx_RecordType;
	JCheckBox chckbx_ObjectCRUD;
	JTextField txt_profileName;
	JTextField metaDirName;

	private static final String btn_file_choose = "btn_file_choose";
	private static final String btn_generate = "btn_generate";

	/**
	 * Create the frame.
	 */
	public ProfileMetaGenerateForm() {
		setTitle("ProfileMetaGenerator_ver1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 460, 449);
		getContentPane().setLayout(null);

		metaDirName = new JTextField();
		metaDirName.setBounds(12, 27, 355, 19);
		getContentPane().add(metaDirName);
		metaDirName.setColumns(10);

		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(147, 282, 131, 30);
		btnGenerate.setActionCommand(btn_generate);
		btnGenerate.addActionListener(this);
		getContentPane().add(btnGenerate);

		chckbx_Application = new JCheckBox("アプリケーション");
		chckbx_Application.setSelected(true);
		chckbx_Application.setBounds(67, 134, 137, 21);
		getContentPane().add(chckbx_Application);

		chckbx_CustomField = new JCheckBox("カスタム項目");
		chckbx_CustomField.setSelected(true);
		chckbx_CustomField.setBounds(67, 167, 137, 21);
		getContentPane().add(chckbx_CustomField);

		chckbx_Apex = new JCheckBox("APEXクラス");
		chckbx_Apex.setSelected(true);
		chckbx_Apex.setBounds(241, 167, 135, 21);
		getContentPane().add(chckbx_Apex);

		chckbx_VFPage = new JCheckBox("VisualForceページ");
		chckbx_VFPage.setSelected(true);
		chckbx_VFPage.setBounds(240, 135, 174, 21);
		getContentPane().add(chckbx_VFPage);

		chckbx_RecordType = new JCheckBox("レコードタイプ");
		chckbx_RecordType.setSelected(true);
		chckbx_RecordType.setBounds(67, 202, 118, 21);
		getContentPane().add(chckbx_RecordType);

		chckbx_Tab = new JCheckBox("カスタムタブ");
		chckbx_Tab.setSelected(true);
		chckbx_Tab.setBounds(241, 202, 137, 21);
		getContentPane().add(chckbx_Tab);

		chckbx_ObjectCRUD = new JCheckBox("オブジェクトCRUD");
		chckbx_ObjectCRUD.setSelected(true);
		chckbx_ObjectCRUD.setBounds(67, 237, 139, 21);
		getContentPane().add(chckbx_ObjectCRUD);

		JLabel label = new JLabel("メタデータ格納先フォルダ");
		label.setBounds(12, 10, 164, 13);
		getContentPane().add(label);

		JButton button = new JButton("参照");
		button.setBounds(373, 26, 61, 21);
		button.addActionListener(this);
		button.setActionCommand(btn_file_choose);
		getContentPane().add(button);

		JLabel label_1 = new JLabel("権限付与");
		label_1.setBounds(12, 110, 103, 13);
		getContentPane().add(label_1);

		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setBounds(12, 348, 422, 52);
		getContentPane().add(textArea);

		JLabel label_2 = new JLabel("出力結果");
		label_2.setBounds(12, 328, 78, 13);
		getContentPane().add(label_2);

		txt_profileName = new JTextField();
		txt_profileName.setColumns(10);
		txt_profileName.setBounds(12, 73, 355, 19);
		getContentPane().add(txt_profileName);

		JLabel label_3 = new JLabel("プロファイル名");
		label_3.setBounds(12, 56, 164, 13);
		getContentPane().add(label_3);

	}

	// ボタン押下処理
	public void actionPerformed(ActionEvent e) {

		try {
			// 参照ボタン
			if (btn_file_choose.equals(e.getActionCommand())) {

				JFileChooser filechooser = new JFileChooser("c:¥¥temp");
				filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int selected = filechooser.showSaveDialog(this);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = filechooser.getSelectedFile();
					metaDirName.setText(file.getAbsolutePath());
				}
			// generateボタン
			} else if (btn_generate.equals(e.getActionCommand())) {

				// 入力チェック
				checkInput();

				// Profileメタデータ作成処理
				ButtonActionParameter param = new ButtonActionParameter();
				param.setProfileName(txt_profileName.getText());
				param.setMetaFilePath(metaDirName.getText());
				param.setChckbx_Tab(chckbx_Tab.isSelected());
				param.setChckbx_Apex(chckbx_Apex.isSelected());
				param.setChckbx_VFPage(chckbx_VFPage.isSelected());
				param.setChckbx_CustomField(chckbx_CustomField.isSelected());
				param.setChckbx_Application(chckbx_Application.isSelected());
				param.setChckbx_RecordType(chckbx_RecordType.isSelected());
				param.setChckbx_ObjectCRUD(chckbx_ObjectCRUD.isSelected());

				ButtonAction action = new ButtonAction(param);
				action.createXML();

				textArea.setText(
						"正常に処理が終了しました。\n" + metaDirName.getText() + "\\" + txt_profileName.getText() + ".profile");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			textArea.setText("【異常終了】\n" + ex.getMessage());
		}
	}

	private void checkInput() throws Exception {

		// メタデータ格納先が設定されていない場合はエラー
		if (metaDirName.getText() == null || metaDirName.getText().isEmpty()) {
			throw new Exception("メタデータ格納先を設定してください。");
		}
		// チェックがすべて外れている場合エラー
		if (!chckbx_Tab.isSelected() && !chckbx_Apex.isSelected() && !chckbx_VFPage.isSelected()
				&& !chckbx_CustomField.isSelected() && !chckbx_Application.isSelected()
				&& !chckbx_RecordType.isSelected()) {
			throw new Exception("権限付与の対象をチェックしてください。");
		}

	}
}
