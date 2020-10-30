import com.tal.file.transform.FileTransformApplication;
import com.tal.file.transform.utils.OssSestore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Description
 * <p>
 * </p>
 * DATE 2019/6/21.
 *
 * @author 刘江涛.
 */
@SpringBootTest(classes = FileTransformApplication.class)
@RunWith(SpringRunner.class)
public class OssTest {

    @Autowired
    private OssSestore ossSestore;

    @Test
    public void test() throws Exception {

        String bucketName = "gc-backup";
        String objectName = "git/1557049348_2019_05_05_gitlab_backup.tar";

        ossSestore.restore(bucketName, objectName);
    }
}
