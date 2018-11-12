package edu.ua.cs.cs495.caladrius.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.github.scribejava.apis.FitbitApi20;
import com.github.scribejava.apis.fitbit.FitBitOAuth2AccessToken;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import edu.ua.cs.cs495.caladrius.User;
import edu.ua.cs.cs495.caladrius.fitbit.FitbitAccount;
import edu.ua.cs.cs495.caladrius.server.ServerAccount;

public class LoginScreen extends AppCompatActivity
{

	private static final String PROTECTED_RESOURCE_URL = "https://api.fitbit.com/1/user/%s/profile.json";

	private static final OAuth20Service service = new ServiceBuilder("22D7HK")
			.apiSecret("0eefb77c8b921283cb5e4477ac063178")
			.scope("activity heartrate location nutrition profile settings sleep social weight") // replace with desired scope
			//your callback URL to store and handle the authorization code sent by Fitbit
			.callback("caladrius://authcallback")
			//.state("some_params")
			.build(FitbitApi20.instance());

	void launchTab(final Context context, final Uri uri){


		CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
			@Override
			public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient client) {
				final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
				final CustomTabsIntent intent = builder.build();
				client.warmup(0L); // This prevents backgrounding after redirection
				intent.launchUrl(context, uri);//pass the url you need to open
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {}
		};
		CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", connection);//mention package name which can handle the CCT their many browser present.
	}



	protected void login(Context cntxt, @NonNull User u)
	{
		Caladrius.user = u;
		Intent pager = new Intent(cntxt, PagerActivity.class);
		startActivity(pager);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// TODO check if the user is already logged in

		setContentView(R.layout.login_screen);

		final Button btnLogin = findViewById(R.id.btnLogin);

		btnLogin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Boolean response_bool;
				try {
					if (Caladrius.user != null && Caladrius.user.fAcc != null && Caladrius.user.fAcc.getPrivateToken() != null){
						Response str_result = new MakeAsyncCall().execute().get();
						response_bool = str_result.getBody().contains(":true");
					}
					else
						response_bool = false;

					if (response_bool) {
						new RefreshAuthToken().execute();
					}
					else {
						final String authorizationUrl = service.getAuthorizationUrl();
						launchTab(v.getContext(), Uri.parse(authorizationUrl));
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		final Button btnTest = findViewById(R.id.btnTest);

        btnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent pager = new Intent(v.getContext(), ListTest.class);
                startActivity(pager);}
        });
	}


	class MakeAsyncCall extends AsyncTask<Void, Void, Response>
	{

		private final String PROTECTED_RESOURCE_URL = "https://api.fitbit.com/1/user/%s/profile.json";

		private final OAuth20Service service = new ServiceBuilder("22D7HK")
				.apiSecret("0eefb77c8b921283cb5e4477ac063178")
				.scope("activity heartrate location nutrition profile settings sleep social weight") // replace with desired scope
				//your callback URL to store and handle the authorization code sent by Fitbit
				.callback("caladrius://authcallback")
				//.state("some_params")
				.build(FitbitApi20.instance());


		protected Response doInBackground(Void... things) {
			try{
				/**
				 * Refresh token
				 */
				//service.refreshAccessToken(accessToken.getRefreshToken());

				/**
				 * Retrieve State of Token
				 */
				final OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.fitbit.com/1.1/oauth2/introspect");
				request.addParameter("token", Caladrius.user.fAcc.getPrivateToken().getAccessToken());


				/**
				 * Get Profile
				 */
				/*final OAuthRequest request = new OAuthRequest(Verb.GET,
						String.format(PROTECTED_RESOURCE_URL, accessToken.getUserId()));*/


				request.addHeader("x-li-format", "json");

				service.signRequest(Caladrius.user.fAcc.getPrivateToken(), request);
				return service.execute(request);
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		protected void onPostExecute(Response response) {
			try {
				//Log.w("RetrieveAuthToken", response.toString());
				//Toast.makeText(LoginScreen.this, response.getBody(), Toast.LENGTH_SHORT).show();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	class RefreshAuthToken extends AsyncTask<Void, Void, FitBitOAuth2AccessToken>
	{

		private final String PROTECTED_RESOURCE_URL = "https://api.fitbit.com/1/user/%s/profile.json";

		private final OAuth20Service service = new ServiceBuilder("22D7HK")
				.apiSecret("0eefb77c8b921283cb5e4477ac063178")
				.scope("activity heartrate location nutrition profile settings sleep social weight") // replace with desired scope
				//your callback URL to store and handle the authorization code sent by Fitbit
				.callback("caladrius://authcallback")
				//.state("some_params")
				.build(FitbitApi20.instance());


		protected FitBitOAuth2AccessToken doInBackground(Void... things) {
			try{
				final OAuth2AccessToken oauth2AccessToken = service.refreshAccessToken(Caladrius.user.fAcc.getPrivateToken().getRefreshToken());

				FitBitOAuth2AccessToken accessToken = (FitBitOAuth2AccessToken) oauth2AccessToken;

				return accessToken;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		protected void onPostExecute(FitBitOAuth2AccessToken accessToken) {
			try {
				Caladrius.user.fAcc.setPrivateToken(accessToken);
				Intent pager = new Intent(LoginScreen.this, PagerActivity.class);
				startActivity(pager);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}