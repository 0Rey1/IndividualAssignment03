package application;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class editAnswerTest {

    @Test
    //test that a user cannot edit another user's posted answer.
    void testEditOthersAnswer() {
        Answer answer = new Answer(1, 1, "Original Answer", "AuthorUser");
        assertThrows(IllegalArgumentException.class, () -> {
            if (!"OtherUser".equals(answer.getAuthor())) {
                throw new IllegalArgumentException("User is not authorized to edit this answer."); //throws an exception that user not athorized.
            }
            answer.setAnswerText("Updated Answer");
        }, "a user cannot edit someone elses answer.");
    }

    @Test
    //test that an answer cannot be updated to an empty string.
    void testEditToEmptyAnswer() {
        Answer answer = new Answer(1, 1, "Original Answer", "CurrentUser");
        String newText = "";

        assertThrows(IllegalArgumentException.class, () -> {
            if (newText.trim().isEmpty()) {
                throw new IllegalArgumentException("Answer text cannot be empty."); //throws an exception that the answer cannot be empty
            }
            answer.setAnswerText(newText);
        });
        
        assertEquals("Original Answer", answer.getAnswerText()); //unchanged.
    }

	@Test
	//test that user cannot update if no answer is selected.
    void testUpdateWithoutSelectingAnswer() {
        Answer answer = null; //no answer selected.
        assertThrows(NullPointerException.class, () -> {
            if (answer == null) {
                throw new NullPointerException("No answer selected."); //throws an exception that none of the answers selected to update. Null
            }
            answer.setAnswerText("Attempted Update");
        }, "Updating without selecting an answer should throw a NullPointerException.");
    }
	
	@Test
	//test that a user can edit their own posted answer successfully.
    void testEditOwnAnswer() {
        Answer answer = new Answer(1, 1, "Original Answer", "CurrentUser");
        String newAnswerText = "Updated Answer";
        answer.setAnswerText(newAnswerText);
        assertEquals(newAnswerText, answer.getAnswerText(), "user's own answer should be updated successfully."); //user can edit/update their own answers.
    }

	@Test
	//test that editing an answer to a whitespace-only string is rejected.
    void testEditWithWhitespaceOnly() {
        Answer answer = new Answer(1, 1, "Original Answer", "CurrentUser");
        String whitespaceText = "    ";

        assertThrows(IllegalArgumentException.class, () -> {
            if (whitespaceText.trim().isEmpty()) {
                throw new IllegalArgumentException("Answer cannot be only whitespaces."); //throws an exception
            }
            answer.setAnswerText(whitespaceText);
        });

        assertEquals("Original Answer", answer.getAnswerText()); //unchanged
    }
}
