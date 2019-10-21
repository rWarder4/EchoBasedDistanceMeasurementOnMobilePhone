package xricht19.distancemeasuring;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class Help extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        // get string text
        TextView helpString = (TextView) findViewById(R.id.helpText);
        Resources res = getResources();

        helpString.setText(res.getString(R.string.help_title)
                + "\n\n\t" + res.getString(R.string.help_1)
                + "\n\t" + res.getString(R.string.help_2)
                + "\n\t" + res.getString(R.string.help_3)
                + "\n\t" + res.getString(R.string.help_4)
                + "\n\t" + res.getString(R.string.help_5)
                + "\n\n" + res.getString(R.string.help_recommended));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
