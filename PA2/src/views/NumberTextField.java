package views;

import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link TextField} which only allows numeric input.
 */
public class NumberTextField extends TextField {

    public NumberTextField() {
        super();
    }

    public NumberTextField(@NotNull String init) {
        super(init);
    }

    @Override
    public void replaceText(int start, int end, @NotNull String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(@NotNull String replacement) {
        if (validate(replacement)) {
            super.replaceSelection(replacement);
        }
    }

    /**
     * Validates that the text field either contains an {@link Integer} or is blank.
     *
     * @param text Text to check.
     * @return True if the text either contains an Integer or is blank.
     */
    private boolean validate(@NotNull String text) {
        // TODO
        if(text.isEmpty()){
            return true;
        }
        try{
            Integer.parseInt(text);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @return Integer value represented by the text in the {@link TextField}.
     * @throws NumberFormatException If {@link NumberTextField#getCharacters()} cannot be parsed into an integer.
     */
    public int getValue() throws NumberFormatException{
        // TODO
        int result=0;
        try{
            result=Integer.parseInt(this.getCharacters().toString());
        }catch (Exception e){
            throw new NumberFormatException("cannot format number");
        }
        return result;
    }
}
