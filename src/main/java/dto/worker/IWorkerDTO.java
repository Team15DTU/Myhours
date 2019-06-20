package dto.worker;

import dto.address.IAddress;
import dto.employer.IEmployerDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface IWorkerDTO {
	
	// Methods
	
	String getFirstName();
	void setFirstName(String firstName);
	
	String getSurName();
	void setSurName(String surName);
	
	String getEmail();
	void setEmail(String email);
	
	String getPassword();
	void setPassword(String password);

	Date getBirthday();
	void setBirthday(Date birthday);
	
	IAddress getHomeAddress();
	void setHomeAddress(IAddress homeAddress);
	
	List<IEmployerDTO> getIEmployers();
	void setIEmployers(List<IEmployerDTO> workPlaces);
	
	int getWorkerID();
	void setWorkerID(int workerID);
}
