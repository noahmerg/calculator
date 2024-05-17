package de.hochschule.trier.ema;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.bigmath.BigMathExpression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;

public class CalculatorActivity extends AppCompatActivity {
    // TODO: Stack would probably be smarter than a string buffer for the input since I have a hard time removing the last input
    private final StringBuffer calcInput = new StringBuffer();
    private int openBraces = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //force nightmode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setListenerUp();
    }
    private void setListenerUp(){
        // Numbers
        findViewById(R.id.button_zero).setOnClickListener(this::numberListener);
        findViewById(R.id.button_one).setOnClickListener(this::numberListener);
        findViewById(R.id.button_two).setOnClickListener(this::numberListener);
        findViewById(R.id.button_three).setOnClickListener(this::numberListener);
        findViewById(R.id.button_four).setOnClickListener(this::numberListener);
        findViewById(R.id.button_five).setOnClickListener(this::numberListener);
        findViewById(R.id.button_six).setOnClickListener(this::numberListener);
        findViewById(R.id.button_seven).setOnClickListener(this::numberListener);
        findViewById(R.id.button_eight).setOnClickListener(this::numberListener);
        findViewById(R.id.button_nine).setOnClickListener(this::numberListener);

        // Signs
        findViewById(R.id.button_brace).setOnClickListener(this::braceListener);
        findViewById(R.id.button_percent).setOnClickListener(this::percentListener);
        findViewById(R.id.button_divide).setOnClickListener(this::signListener);
        findViewById(R.id.button_multiply).setOnClickListener(this::signListener);
        findViewById(R.id.button_minus).setOnClickListener(this::signListener);
        findViewById(R.id.button_plus).setOnClickListener(this::signListener);
        findViewById(R.id.button_comma).setOnClickListener(this::commaListener);
        findViewById(R.id.button_plusminus).setOnClickListener(v -> changeSignListener());

        // Clear
        findViewById(R.id.button_clear).setOnClickListener(this::clearListener);

        // Equal
        findViewById(R.id.button_equal).setOnClickListener(this::equalListener);

        // Tools
        findViewById(R.id.button_history).setOnClickListener(v -> showHistory());
        findViewById(R.id.button_unit).setOnClickListener(v -> showUnitCalculator());
        findViewById(R.id.button_scientific).setOnClickListener(v -> switchCalculatorModes());
        findViewById(R.id.button_backspace).setOnClickListener(v -> deleteLastCharOfInput());

        // Scientific
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return;
        }
        findViewById(R.id.button_switch_scientific_side).setOnClickListener(v -> switchScientificSide());
        findViewById(R.id.button_switch_rad_deg).setOnClickListener(v -> switchRadDeg());
        findViewById(R.id.button_sqrt).setOnClickListener(v -> addSqrt());
        findViewById(R.id.button_sin).setOnClickListener(this::addTrigonometricFunction);
        findViewById(R.id.button_cos).setOnClickListener(this::addTrigonometricFunction);
        findViewById(R.id.button_tan).setOnClickListener(this::addTrigonometricFunction);
        findViewById(R.id.button_ln).setOnClickListener(this::addLogarithm);
        findViewById(R.id.button_log).setOnClickListener(this::addLogarithm);
        findViewById(R.id.button_mult_inverse).setOnClickListener(v -> addMultInverse());
        findViewById(R.id.button_e_power_x).setOnClickListener(v -> addEPowerX());
        findViewById(R.id.button_x_power_2).setOnClickListener(v -> addXPower2());
        findViewById(R.id.button_x_power_y).setOnClickListener(v -> addXPowerY());
        findViewById(R.id.button_abs).setOnClickListener(v -> addAbs());
        findViewById(R.id.button_pi).setOnClickListener(this::addPiOrE);
        findViewById(R.id.button_e).setOnClickListener(this::addPiOrE);
    }


    private void addPiOrE(View v) {
        String clickedButtonText = ((Button) v).getText().toString();
        if(calcInput.length() == 0)
        {
            calcInput.append(clickedButtonText);
            updateText();
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar) || lastChar == ')' || lastChar == '%' || lastChar == 'π' || lastChar == 'e')
        {
            calcInput.append("*").append(clickedButtonText);
        } else if (lastChar == '(' || isNormalSign(lastChar)) {
            calcInput.append(clickedButtonText);
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addAbs() {
        if(calcInput.length() == 0)
        {
            calcInput.append("abs(");
            openBraces++;
            updateText();
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar)|| lastChar == ')') {
            calcInput.append("*abs(");
            openBraces++;
        } else if (lastChar == '(' || isNormalSign(lastChar)) {
            calcInput.append("abs(");
            openBraces++;
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addXPowerY() {
        if(calcInput.length() == 0) {
            makeToast("Invalid input");
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if (isDigit(lastChar) || lastChar == ')') {
            calcInput.append("^(");
            openBraces++;
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addXPower2() {
        if(calcInput.length() == 0) {
            makeToast("Invalid input");
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar) || lastChar == ')') {
            calcInput.append("^(2)");
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addEPowerX() {
        if(calcInput.length() == 0) {
            calcInput.append("e^(");
            openBraces++;
            updateText();
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar) || lastChar == ')') {
            calcInput.append("*e^(");
            openBraces++;
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addMultInverse() {
        if(calcInput.length() == 0) {
            calcInput.append("1/").append("(");
            openBraces++;
            updateText();
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar) || lastChar == ')') {
            calcInput.append("*").append("1/").append("(");
            openBraces++;
        } else if (lastChar == '(' || isNormalSign(lastChar)) {
            calcInput.append("1/(");
            openBraces++;
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addLogarithm(View v) {
        String clickedButtonText = ((Button) v).getText().toString();
        if(calcInput.length() == 0) {
            calcInput.append(clickedButtonText).append("(");
            openBraces++;
            updateText();
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar)|| lastChar == ')') {
            calcInput.append("*").append(clickedButtonText).append("(");
            openBraces++;
        } else if (lastChar == '(' || isNormalSign(lastChar)) {
            calcInput.append(clickedButtonText).append("(");
            openBraces++;
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addTrigonometricFunction(View v) {
        String clickedButtonText = ((Button) v).getText().toString();
        if(calcInput.length() == 0)
        {
            calcInput.append(clickedButtonText).append("(");
            openBraces++;
            updateText();
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar) || lastChar == ')')
        {
            calcInput.append("*").append(clickedButtonText).append("(");
            openBraces++;
        } else if (lastChar == '(' || isNormalSign(lastChar)) {
            calcInput.append(clickedButtonText).append("(");
            openBraces++;
        } else{
            makeToast("Invalid input");
        }
        updateText();
    }

    private void addSqrt() {
        if(calcInput.length() == 0) {
            calcInput.append("√(");
            openBraces++;
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar) || lastChar == ')') {
            calcInput.append("*√(");
            openBraces++;
        } else if (lastChar == '(' || isNormalSign(lastChar)){
            calcInput.append("√(");
            openBraces++;
        } else {
            makeToast("Invalid input");
        }
        updateText();
    }

    private void switchRadDeg() {
        makeToast("Rad/deg switch coming soon");
    }

    private void switchScientificSide() {
        makeToast("Scientific side switch coming soon");
    }

    private void showHistory() {
        makeToast("History coming soon");
    }
    private void showUnitCalculator() {
        makeToast("Unit Calculator coming soon");
    }
    private void switchCalculatorModes() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        updateText();
    }
    private void deleteLastCharOfInput() {
        if(calcInput.length() == 0) return;
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(lastChar == '(') {
            if(calcInput.length() >= 2){
                char secondLastChar = calcInput.charAt(calcInput.length()-2);
                if(secondLastChar == 's' || secondLastChar == 'n' || secondLastChar == 'g') {
                    calcInput.delete(calcInput.length()-4, calcInput.length());
                }
            }
            openBraces--;
        } else if(lastChar == ')') {
            openBraces++;
        }
        calcInput.deleteCharAt(calcInput.length()-1);
        updateText();
    }
    public void numberListener(View v) {
        String clickedButtonText = ((Button) v).getText().toString();
        if (calcInput.length() != 0
                && (calcInput.charAt(calcInput.length() - 1) == ')'
                || calcInput.charAt(calcInput.length() - 1) == '%'
                || calcInput.charAt(calcInput.length() - 1) == 'π'
                || calcInput.charAt(calcInput.length() - 1) == 'e')) {
            calcInput.append("*");
        }
        calcInput.append(clickedButtonText);
        updateText();
    }

    public void signListener(View v) {
        String clickedButtonText = ((Button) v).getText().toString();

        if (calcInput.length()== 0) {
            makeToast("Invalid input");
            return;
        }

        char lastChar = calcInput.charAt(calcInput.length()-1);

        if (isDigit(lastChar) || lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '%'){
            calcInput.append(clickedButtonText);
        } else {
            makeToast("Invalid input");
        }

        updateText();
    }


    private void commaListener(View view) {
        if(calcInput.length() == 0) {
            makeToast("Invalid input");
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar)) {
            calcInput.append(",");
        } else {
            makeToast("Invalid input");
        }
        updateText();
    }

    private void percentListener(View view) {
        if(calcInput.length() == 0) {
            makeToast("Invalid input");
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length()-1);
        if(isDigit(lastChar) || lastChar == ')' || lastChar == 'π' || lastChar == 'e') {
            calcInput.append("%");
        } else {
            makeToast("Invalid input");
        }
        updateText();
    }

    private void braceListener(View view) {
        if (calcInput.length() == 0) {
            calcInput.append("(");
            openBraces++;
            return;
        }
        char lastChar = calcInput.charAt(calcInput.length() - 1);
        if (lastChar >= '0' && lastChar <= '9' || lastChar == ')' || lastChar == '%' || lastChar == 'π' || lastChar == 'e') {
            if (openBraces > 0) {
                calcInput.append(")");
                openBraces--;
            } else {
                calcInput.append("*(");
                openBraces++;
            }
        } else if (lastChar == '(' || lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/') {
            calcInput.append("(");
            openBraces++;
        } else {
            makeToast("Invalid input");
        }
        updateText();
    }
    private void changeSignListener() {
        //input is empty
        if (calcInput.length() == 0) {
            calcInput.append("(-");
            openBraces++;
        }
        char lastChar = calcInput.charAt(calcInput.length() - 1);
        //last char is a number:
        if (isDigit(lastChar)) {
            for (int i = calcInput.length() - 1; i >= 0; i--) {
                if (i == 0) {
                    calcInput.insert(i, "(-");
                    openBraces++;
                    break;
                }
                if (calcInput.charAt(i - 1) < '0' || calcInput.charAt(i - 1) > '9') {
                    calcInput.insert(i, "(-");
                    openBraces++;
                    break;
                }
            }
        } else if(isNormalSign(lastChar)) {
            calcInput.append("(-");
            openBraces++;
            calcInput.replace(calcInput.length()-1, calcInput.length(), "-");
        }else if (lastChar == ')'){
            calcInput.append("*(-");
            openBraces++;
        } else if (lastChar == '(') {
            calcInput.append("(-");
            openBraces++;
        } else {
            makeToast("Invalid input");
        }
        updateText();
    }

    public void clearListener(View v) {
        calcInput.setLength(0);
        openBraces = 0;
        updateText();
    }

    /**
     * This method uses the Expression class from the EvalEx library and the EvalEx-big-math.
     * <a href="https://github.com/ezylang/EvalEx">More about EvalEx</a>
     * <a href="https://github.com/ezylang/EvalEx-big-math">More about EvalEx-big-math</a>
     */
    public void equalListener(View v) {
        makeInputEvaluable();
        ExpressionConfiguration configuration = ExpressionConfiguration.builder()
                .decimalPlacesResult(4)
                .build();
        try {
            String result = new BigMathExpression(calcInput.toString(), configuration).evaluate().getStringValue();
            result = result.replace('.', ',');
            calcInput.setLength(0);
            calcInput.append(result);
        } catch (EvaluationException | ParseException e) {
            calcInput.setLength(0);
            calcInput.append("Error");
            updateText();
            calcInput.setLength(0);
            return;
        }
        openBraces = 0;
        updateText();
    }

    private void makeInputEvaluable() {
        for (int i = 0; i < calcInput.length(); i++) {
            if (calcInput.charAt(i) == '%') {
                calcInput.setCharAt(i, '/');
                calcInput.insert(i + 1, "100");
            } else if (calcInput.charAt(i) == '×') {
                calcInput.setCharAt(i, '*');
            } else if (calcInput.charAt(i) == '÷') {
                calcInput.setCharAt(i, '/');
            } else if (calcInput.charAt(i) == ',') {
                calcInput.setCharAt(i, '.');
            } else if (calcInput.charAt(i) == 'π') {
                calcInput.replace(i, i + 1, "PI()");
            } else if (calcInput.charAt(i) == 'e') {
                calcInput.replace(i, i + 1, "E()");
            } else if (calcInput.charAt(i) == '√') {
                calcInput.replace(i, i + 1, "SQRT");
            } else if (calcInput.charAt(i) == 'l') {
                if(calcInput.charAt(i+1) == 'n') {
                    calcInput.replace(i, i + 2, "LOG");
                } else {
                    calcInput.replace(i, i + 3, "LOG10");
                }
            }
        }
        for (int i = 0; i < openBraces; i++) {
            calcInput.append(")");
            openBraces--;
        }
    }

    private void updateText() {
        ((TextView)findViewById(R.id.textViewCalcInput)).setText(calcInput);
    }
    private void makeToast(String output) {
        Context context = getApplicationContext(); // or getContext().getApplicationContext() if inside a fragment
        Toast.makeText(context, output, Toast.LENGTH_SHORT).show();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isNormalSign(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷';
    }
}