package vander.gabriel.intents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import vander.gabriel.intents.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final int openInBrowserMenuItem = R.id.openInBrowserMenuItem;
        final int dialPhoneNumberMenuItem = R.id.dialPhoneNumberMenuItem;

        switch (id) {
            case openInBrowserMenuItem:
                openParameterAsWebLink();
                return true;
            case dialPhoneNumberMenuItem:
                dialParameterAsPhoneNumber();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openParameterAsWebLink() {
        String parameter = mainViewModel.getParameter().getValue();
        parameter = getParameterAsUrl(parameter);

        if (parameter != null) {
            launchActionIntent(parameter, Intent.ACTION_VIEW);
        }
    }

    private void dialParameterAsPhoneNumber() {
        String parameter = mainViewModel.getParameter().getValue();
        parameter = getParameterAsPhoneNumber(parameter);

        if (parameter != null) {
            launchActionIntent(parameter, Intent.ACTION_DIAL);
        }
    }

    private void launchActionIntent(String parameter, String actionDial) {
        Intent actionViewIntent = new Intent(actionDial);
        actionViewIntent.setData(Uri.parse(parameter));
        startActivity(actionViewIntent);
    }

    private String getParameterAsUrl(String parameter) {
        if (parameter == null) return null;

        if (!parameter.startsWith("https://") && !parameter.startsWith("http://")) {
            parameter = "http://" + parameter;
        }

        return URLUtil.isValidUrl(parameter) ? parameter : null;
    }

    private String getParameterAsPhoneNumber(String parameter) {
        if (parameter == null) return null;

        if (!PhoneNumberUtils.isGlobalPhoneNumber(parameter)) return null;

        return String.format("tel: %s", parameter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}