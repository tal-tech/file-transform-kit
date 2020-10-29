import com.tal.cloud.storage.StorageApplication;
import com.tal.cloud.storage.node.customer.Customers;
import com.tal.cloud.storage.node.image.ImageTasks;
import com.tal.cloud.storage.node.media.MediaTasks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Description
 * <p>
 * </p>
 * DATE 2019/6/19.
 *
 * @author 刘江涛.
 */
@SpringBootTest(classes = StorageApplication.class)
@RunWith(SpringRunner.class)
public class ConfigTest {

    @Autowired
    private Customers customers;

    @Autowired
    private MediaTasks mediaTasks;


    @Autowired
    private ImageTasks imageTasks;

    @Test
    public void customer() {
        System.out.println(customers.auth("test-abc", "*"));
        System.out.println(mediaTasks.find("test-abc"));
        System.out.println(imageTasks.find("test-abc"));
    }
}
