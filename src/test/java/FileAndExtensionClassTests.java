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
        fileAndExtension = new FileAndExtension(file);
        try {
            fileAndExtension.readFileNameAndExtension();
        } catch (Exception e) {
            Assertions.fail("Exception thrown when calling readFileNameAndExtensions()");
        }
        Assertions.assertEquals("name", fileAndExtension.getFileName());
        Assertions.assertEquals("extension", fileAndExtension.getFileExtension());
    }

    @Test
    void MethodReadFileNameAndExtensionReadsThrowsExceptionWhenFileNameContainsSlashes() {
        Mockito.when(file.getName()).thenReturn("n/ame.extension");
        fileAndExtension = new FileAndExtension(file);
        Assertions.assertThrows(IllegalFileNameException.class, () -> fileAndExtension.readFileNameAndExtension());
    }

    @Test
    void MethodReadFileNameAndExtensionReadsThrowsExceptionWhenFileExtensionOrNameIsEmpty() {
        Mockito.when(file.getName()).thenReturn("name.");
        fileAndExtension = new FileAndExtension(file);
        Assertions.assertThrows(IllegalFileNameException.class, () -> fileAndExtension.readFileNameAndExtension());

        Mockito.when(file.getName()).thenReturn(".extension");
        fileAndExtension = new FileAndExtension(file);
        Assertions.assertThrows(IllegalFileNameException.class, () -> fileAndExtension.readFileNameAndExtension());

        Mockito.when(file.getName()).thenReturn(".");
        fileAndExtension = new FileAndExtension(file);
        Assertions.assertThrows(IllegalFileNameException.class, () -> fileAndExtension.readFileNameAndExtension());
    }
}
