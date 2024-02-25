package org.akop.crosswordtest;

import static org.akop.ararat.core.CrosswordKt.buildCrossword;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import org.akop.ararat.core.Crossword;
import org.akop.ararat.io.PuzFormatter;
import org.akop.ararat.view.CrosswordView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CrosswordView.OnLongPressListener,
        CrosswordView.OnStateChangeListener, CrosswordView.OnSelectionChangeListener {

    private CrosswordView crosswordView;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crosswordView = findViewById(R.id.crossword);
        hint = findViewById(R.id.hint);

        Crossword puzzle = readPuzzle(R.raw.puzzle);

        setTitle(getString(R.string.title_by_author, puzzle.getTitle(), puzzle.getAuthor()));

        crosswordView.setCrossword(puzzle);
        crosswordView.setOnLongPressListener(this);
        crosswordView.setOnStateChangeListener(this);
        crosswordView.setOnSelectionChangeListener(this);
        crosswordView.setInputValidator(ch -> !Character.isISOControl(ch.charAt(0)));
        crosswordView.setUndoMode(CrosswordView.UNDO_NONE);
        crosswordView.setMarkerDisplayMode(CrosswordView.MARKER_CHEAT);

        onSelectionChanged(crosswordView, crosswordView.getSelectedWord(), crosswordView.getSelectedCell());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        crosswordView.restoreState(savedInstanceState.getParcelable("state"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("state", crosswordView.getState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_restart:
                crosswordView.reset();
                return true;
            case R.id.menu_solve_cell:
                crosswordView.solveChar(crosswordView.getSelectedWord(), crosswordView.getSelectedCell());
                return true;
            case R.id.menu_solve_word:
                crosswordView.solveWord(crosswordView.getSelectedWord());
                return true;
            case R.id.menu_solve_puzzle:
                crosswordView.solveCrossword();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCellLongPressed(CrosswordView view, Crossword.Word word, int cell) {
        Toast.makeText(this, "Show popup menu for " + word.getHint(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCrosswordChanged(CrosswordView view) {
    }

    @Override
    public void onCrosswordSolved(CrosswordView view) {
        Toast.makeText(this, R.string.youve_solved_the_puzzle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCrosswordUnsolved(CrosswordView view) {
    }

    private Crossword readPuzzle(@RawRes int resourceId) {
        return buildCrossword(builder -> {
            try {
                new PuzFormatter().read(builder, getResources().openRawResource(resourceId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }


    @Override
    public void onSelectionChanged(CrosswordView view, Crossword.Word word, int position) {
        if (word != null) {
            String hintText = "";
            switch (word.getDirection()) {
                case Crossword.Word.DIR_ACROSS:
                    hintText = getString(R.string.across, word.getNumber(), word.getHint());
                    break;
                case Crossword.Word.DIR_DOWN:
                    hintText = getString(R.string.down, word.getNumber(), word.getHint());
                    break;
            }
            hint.setText(hintText);
        }
    }
}
