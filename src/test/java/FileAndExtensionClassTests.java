import com.milbar.logic.FileAndExtension;
import com.milbar.logic.exceptions.IllegalFileNameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Mockito.mock;

class FileAndExtensionClassTests {

    @Mock
    private File file = mock(File.class);
    private FileAndExtension fileAndExtension;

    @Test
    void MethodReadFileNameAndExtensionReadsCorrectlyExtensionName() {
        Mockito.when(file.getName()).thenReturn("name.extension");
        try {
            fileAndExtension = new FileAndExtension(file);
        } catch (Exception e) {
            Assertions.fail("Exception thrown when calling readFileNameAndExtensions()");
        }
        Assertions.assertEquals("name", fileAndExtension.getFileName());
        Assertions.assertEquals("extension", fileAndExtension.getFileExtension());
    }

    @Test
    void MethodReadFileNameAndExtensionReadsThrowsExceptionWhenFileNameContainsSlashes() {
        Mockito.when(file.getName()).thenReturn("n/ame.extension");
        Assertions.assertThrows(IllegalFileNameException.class, () -> new FileAndExtension(file));
    }

    @Test
    void MethodReadFileNameAndExtensionReadsThrowsExceptionWhenFileExtensionOrNameIsEmpty() {
        Mockito.when(file.getName()).thenReturn("name.");
        Assertions.assertThrows(IllegalFileNameException.class, () -> new FileAndExtension(file));

        Mockito.when(file.getName()).thenReturn(".extension");
        Assertions.assertThrows(IllegalFileNameException.class, () -> new FileAndExtension(file));

        Mockito.when(file.getName()).thenReturn(".");
        Assertions.assertThrows(IllegalFileNameException.class, () -> new FileAndExtension(file));
    }
}
