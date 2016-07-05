package dragora.net.innovationcontest2016;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thebluealliance.spectrum.SpectrumDialog;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

  DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test");
  HashMap<String, String> obj = new HashMap<String, String>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    obj.put("kid", "fighino224");
    obj.put("texture",
        "https://scontent.xx.fbcdn.net/v/t1.0-9/10431668_10205233708436551_1811721222502137069_n.jpg?oh=c09b432dbf2bc99cf0019ea575539e42&oe=5835234F");
    obj.put("shape", "circle");

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.remove("action");
        ref.push().setValue(obj);
      }
    });

    FloatingActionButton fabJumpLeft = (FloatingActionButton) findViewById(R.id.fabJumpLeft);
    fabJumpLeft.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.put("action", "ljump");
        ref.push().setValue(obj);
      }
    });
    FloatingActionButton fabJumpRight = (FloatingActionButton) findViewById(R.id.fabJumpRight);
    fabJumpRight.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        obj.put("date", "" + new Date().getTime() / 1000);
        obj.put("action", "rjump");
        ref.push().setValue(obj);
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void onShapeClick(View view) {
    switch (view.getId()) {
      case R.id.circle:
        obj.put("shape", "circle");
        break;
      case R.id.square:
        obj.put("shape", "square");
        break;
    }
  }

  public void onColorClick(final View v) {
    new SpectrumDialog.Builder(getBaseContext()).setColors(R.array.demo_colors)
        .setSelectedColorRes(R.color.md_blue_500)
        .setDismissOnColorSelected(true)
        .setFixedColumnCount(4)
        .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
          @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
            if (positiveResult) {
              obj.put("color", Integer.toHexString(color).toUpperCase().substring(2));
              v.setBackgroundColor(color);
              Toast.makeText(getContext(),
                  "Color selected: #" + Integer.toHexString(color).toUpperCase(),
                  Toast.LENGTH_SHORT).show();
            } else {

            }
          }
        })
        .build()
        .show(getSupportFragmentManager(), "dialog_demo_4");
  }

  public Context getContext() {
    return getBaseContext();
  }
}
