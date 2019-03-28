package jp.co.ncast.form;

public class ButtonActionParameter {

	private String metaFilePath = null;
	private String profileName = null;
	private boolean chckbx_Tab = false;
	private boolean chckbx_Apex = false;
	private boolean chckbx_VFPage = false;
	private boolean chckbx_CustomField = false;
	private boolean chckbx_Application = false;
	private boolean chckbx_RecordType = false;
	private boolean chckbx_ObjectCRUD = false;

	public boolean isChckbx_ObjectCRUD() {
		return chckbx_ObjectCRUD;
	}

	public void setChckbx_ObjectCRUD(boolean chckbx_ObjectCRUD) {
		this.chckbx_ObjectCRUD = chckbx_ObjectCRUD;
	}

	public String getMetaFilePath() {
		return metaFilePath;
	}

	public void setMetaFilePath(String metaFilePath) {
		this.metaFilePath = metaFilePath;
	}

	public boolean isChckbx_Tab() {
		return chckbx_Tab;
	}

	public void setChckbx_Tab(boolean chckbx_Tab) {
		this.chckbx_Tab = chckbx_Tab;
	}

	public boolean isChckbx_Apex() {
		return chckbx_Apex;
	}

	public void setChckbx_Apex(boolean chckbx_Apex) {
		this.chckbx_Apex = chckbx_Apex;
	}

	public boolean isChckbx_VFPage() {
		return chckbx_VFPage;
	}

	public void setChckbx_VFPage(boolean chckbx_VFPage) {
		this.chckbx_VFPage = chckbx_VFPage;
	}

	public boolean isChckbx_CustomField() {
		return chckbx_CustomField;
	}

	public void setChckbx_CustomField(boolean chckbx_CustomField) {
		this.chckbx_CustomField = chckbx_CustomField;
	}

	public boolean isChckbx_Application() {
		return chckbx_Application;
	}

	public void setChckbx_Application(boolean chckbx_Application) {
		this.chckbx_Application = chckbx_Application;
	}

	public boolean isChckbx_RecordType() {
		return chckbx_RecordType;
	}

	public void setChckbx_RecordType(boolean chckbx_RecordType) {
		this.chckbx_RecordType = chckbx_RecordType;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
}
