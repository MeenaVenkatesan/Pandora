package com.pandora.mashup;


	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.net.URL;
	import java.net.URLConnection;
	import java.util.ArrayList;
	import java.util.List;

	import org.apache.http.NameValuePair;
	import org.apache.http.message.BasicNameValuePair;

	import com.google.gson.JsonArray;
	import com.google.gson.JsonElement;
	import com.google.gson.JsonObject;
	import com.google.gson.JsonParser;
	import com.google.gson.stream.JsonReader;
	import com.pandora.mashup.Rows;
	import com.twilio.sdk.TwilioRestClient;
	import com.twilio.sdk.TwilioRestException;
	import com.twilio.sdk.resource.factory.CallFactory;
	import com.twilio.sdk.resource.instance.Call;

	/**
	 * @author MVenkatesan This class takes data from fusion table via rest call and
	 *         invoke twilio to make phone calls with standard message. Twilio
	 *         account is an trial account and therefore possible to send only demo
	 *         text provided by twilio. Please register with twilio the phone
	 *         numbers and update in fusion table ->
	 *         https://www.google.com/fusiontables
	 *         /DataSource?docid=11YF6PUn1Yd5z74LKeuiQZ2m4SNjINkK40sZpvIM
	 *         &pli=1#rows:id=1 
	 *         If the twilio is upgraded then its possible to send custom text
	 * 
	 */
	public class FusionTwilio {

		public static final String ACCOUNT_SID = "AC5da4c2f33a687a10f0a4a6d96814d134";
		public static final String AUTH_TOKEN = "7e4b486dc0987ea55531bd448e971f87";

		public static void main(String... args) throws IOException {

			URL url = new URL(
					"https://www.googleapis.com/fusiontables/v1/query?sql=select+*+from+11YF6PUn1Yd5z74LKeuiQZ2m4SNjINkK40sZpvIM&hdrs=false&typed=true&key=AIzaSyAa2ct1byw889hbUSyoG6zPC6WGbSOQ2no");
			URLConnection connection = url.openConnection();
			JsonReader reader = new JsonReader(new InputStreamReader(connection
					.getInputStream()));

			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObj = jsonParser.parse(reader).getAsJsonObject();
			JsonArray rowsData = jsonObj.getAsJsonArray("rows");

			String[] strArray;
			String stTemp;
			Rows rows;
			List<Rows> dataRows = new ArrayList<Rows>();
			for (JsonElement rowElement : rowsData) {
				rows = new Rows();
				stTemp = rowElement.toString();
				stTemp = stTemp.replace("[", "");
				stTemp = stTemp.replace("]", "");
				strArray = stTemp.split(",");
				rows.setTextData(strArray[0]);
				rows.setPhoneNumber(strArray[1]);
				dataRows.add(rows);

			}

			TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
			Call call;
			CallFactory callFactory;

			for (Rows data : dataRows) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("To", data.getPhoneNumber()));
				params.add(new BasicNameValuePair("From", "+18589055732"));
				// Can be replaced with rows.getMessage() for upgraded twilio
				// account
				params.add(new BasicNameValuePair("Url",
						"http://demo.twilio.com/docs/voice.xml"));
				params.add(new BasicNameValuePair("Method", "GET"));
				params.add(new BasicNameValuePair("FallbackMethod", "GET"));
				params.add(new BasicNameValuePair("StatusCallbackMethod", "GET"));
				params.add(new BasicNameValuePair("Record", "false"));

				callFactory = client.getAccount().getCallFactory();

				try {
					call = callFactory.create(params);
					System.out.println(call.getSid());
				} catch (TwilioRestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
}
