/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    FastDictionary sdict;
    TextView txtWord;
    TextView label;
    Button reset;
    Button challenge;
    final Handler handler = new Handler();
    private boolean userTurn = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        txtWord = (TextView)findViewById(R.id.ghostText);
        label = (TextView)findViewById(R.id.gameStatus);
        reset = (Button)findViewById(R.id.reset);
        challenge = (Button)findViewById(R.id.challenge);
        AssetManager assetManager = getAssets();
        try {
            sdict = new FastDictionary(assetManager.open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        onStart(null);
    }

    public void challenge(View view) {
        String word = txtWord.getText().toString();
        if(word.length()>=dictionary.MIN_WORD_LENGTH && sdict.isWord(word))
            label.setText("User Wins !");
        else
            {
                String new_letter = sdict.getAnyWordStartingWith(txtWord.getText().toString());
                if(new_letter==null)
                    label.setText("User Wins !");
                else
                    label.setText("Computer Wins !");
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        challenge.setEnabled(true);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        String word = txtWord.getText().toString();
        int index = txtWord.getText().toString().length();
        if(word.length() >= dictionary.MIN_WORD_LENGTH && sdict.isWord(word))
        {
            label.setText("Computer Wins !");
            challenge.setEnabled(false);
            return;
        }
        String new_letter = sdict.getAnyWordStartingWith(txtWord.getText().toString());
        if(new_letter == null)
        {
            label.setText("Computer Wins !");
            challenge.setEnabled(false);
            return;
        }
        else
            txtWord.setText(txtWord.getText().toString()+new_letter.charAt(index));
        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if(Character.isLetter(c))
        {
            txtWord.setText(txtWord.getText().toString()+c);
            userTurn = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();
                }
            }, 1000);
            label.setText(COMPUTER_TURN);
            return true;
        }
        else
        {
            Toast.makeText(this, "Enter An Alphabet", Toast.LENGTH_SHORT).show();
            return super.onKeyUp(keyCode, event);
        }
    }
}
