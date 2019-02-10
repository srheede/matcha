package wethinkcode.co.za.matcha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
  //  DatabaseConnection Database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registerBttn = findViewById(R.id.registerBttn);
        registerBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoRegister = new Intent(getApplicationContext(), Register.class);
                startActivity(gotoRegister);
            }
        });

        Button createDatabaseBttn = findViewById(R.id.createDatabaseBttn);
        createDatabaseBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    // create our mysql database connection
                    String myDriver = "org.gjt.mm.mysql.Driver";
                    String myUrl = "jdbc:mysql://localhost/camagru";
                    Class.forName(myDriver);
                    Connection conn = DriverManager.getConnection(myUrl, "root", "punchbuggy");

                    // our SQL SELECT query.
                    // if you only need a few columns, specify them by name instead of using "*"
                    String query = "SELECT * FROM users";

                    // create the java statement
                    Statement st = conn.createStatement();

                    // execute the query, and get a java result set
                    ResultSet rs = st.executeQuery(query);

                    // iterate through the java result set
                    while (rs.next())
                    {
                        String Username = rs.getString("username");

                        // print the results
                        System.out.format("%s\n", Username);
                    }
                    st.close();
                }
                catch (Exception e)
                {
                    System.err.println("Got an exception! ");
                    System.err.println(e.getMessage());
                }
            }
        });

       // Database = new DatabaseConnection(this);
    }
}
