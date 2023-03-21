package database.validation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GenerateFileName {

	public static String generateFileName() {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat(StringConstants.dateFormat);
		String strDate = dateFormat.format(date);
		return new String(StringConstants.Report + StringConstants.whitespace + strDate);
	}

}
