package br.com.orionsoft.monstrengo.crud.report.entities;

import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;

public abstract class ReportParam
{
	public static final String PROPERTY_PATH_LABEL_SEPARATOR = "> ";
	
	public ReportParam(UserReport userReport)
	{
		this.userReport = userReport;
	}
	
	private UserReport userReport;

	public UserReport getUserReport(){return userReport;}	
}
