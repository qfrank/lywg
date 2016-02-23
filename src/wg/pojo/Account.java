package wg.pojo;

import wg.pojo.config.AccountSchema;

public class Account {

	String accFile;
	
	String userId;

	String password;

	String securityCode;

	String encrptString;

	AccountSchema accountSchema;
	
	public Account() {
	}

	public Account(String userId, String password, String securityCode,
			String encrptString) {
		this.userId = userId;
		this.password = password;
		this.securityCode = securityCode;
		this.encrptString = encrptString;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getEncrptString() {
		return encrptString;
	}

	public void setEncrptString(String encrptString) {
		this.encrptString = encrptString;
	}

	public String getAccFile() {
		return accFile;
	}

	public void setAccFile(String accFile) {
		this.accFile = accFile;
	}

	public AccountSchema getAccountSchema() {
		return accountSchema;
	}

	public void setAccountSchema(AccountSchema accountSchema) {
		this.accountSchema = accountSchema;
	}

	@Override
	public String toString() {
		return "Account{" +
				"accFile='" + accFile + '\'' +
				", userId='" + userId + '\'' +
				", password='" + password + '\'' +
				", securityCode='" + securityCode + '\'' +
				", encrptString='" + encrptString + '\'' +
				", accountSchema=" + accountSchema +
				'}';
	}
}
