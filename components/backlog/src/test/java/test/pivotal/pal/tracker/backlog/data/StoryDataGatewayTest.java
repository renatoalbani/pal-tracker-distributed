package test.pivotal.pal.tracker.backlog.data;

import io.pivotal.pal.tracker.backlog.data.StoryDataGateway;
import io.pivotal.pal.tracker.backlog.data.StoryFields;
import io.pivotal.pal.tracker.backlog.data.StoryRecord;
import io.pivotal.pal.tracker.testsupport.TestScenarioSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static io.pivotal.pal.tracker.backlog.data.StoryFields.storyFieldsBuilder;
import static io.pivotal.pal.tracker.backlog.data.StoryRecord.storyRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class StoryDataGatewayTest {

    private TestScenarioSupport testScenarioSupport = new TestScenarioSupport("tracker_backlog_test");
    private JdbcTemplate template = testScenarioSupport.template;
    private StoryDataGateway gateway = new StoryDataGateway(testScenarioSupport.dataSource);

    @Before
    public void setUp() throws Exception {
        template.execute("DELETE FROM stories;");
    }

    @Test
    public void testCreate() {
        StoryFields fields = storyFieldsBuilder()
            .projectId(22L)
            .name("aStory")
            .build();


        StoryRecord created = gateway.create(fields);


        assertThat(created.id).isNotNull();
        assertThat(created.name).isEqualTo("aStory");
        assertThat(created.projectId).isEqualTo(22L);

        Map<String, Object> persisted = template.queryForMap("select * from stories where id = ?", created.id);

        assertThat(persisted.get("project_id")).isEqualTo(22L);
        assertThat(persisted.get("name")).isEqualTo("aStory");
    }

    @Test
    public void testFindBy() {
        template.execute("insert into stories (id, project_id, name) values (1346, 22, 'aStory')");


        List<StoryRecord> result = gateway.findAllByProjectId(22L);


        assertThat(result).containsExactly(
            storyRecordBuilder()
                .id(1346L)
                .projectId(22L)
                .name("aStory")
                .build()
        );
    }

    @Test
    public void testListLastStories(){
        template.execute("insert into stories (id, project_id, name) values (1346, 22, 'aStory1')");
        template.execute("insert into stories (id, project_id, name) values (1347, 22, 'aStory2')");
        template.execute("insert into stories (id, project_id, name) values (1348, 22, 'aStory3')");
        template.execute("insert into stories (id, project_id, name) values (1349, 22, 'aStory4')");
        template.execute("insert into stories (id, project_id, name) values (1350, 22, 'aStory5')");
        template.execute("insert into stories (id, project_id, name) values (1351, 22, 'aStory6')");

        List<StoryRecord> lastStories = gateway.findLastStories(5);

        assertEquals(5, lastStories.size());

        assertThat(lastStories).containsSequence(
                storyRecordBuilder()
                        .id(1351)
                        .projectId(22)
                        .name("aStory6").build(),
                storyRecordBuilder()
                        .id(1350)
                        .projectId(22)
                        .name("aStory5").build(),
                storyRecordBuilder()
                        .id(1349)
                        .projectId(22)
                        .name("aStory4").build(),
                storyRecordBuilder()
                        .id(1348)
                        .projectId(22)
                        .name("aStory3").build(),
                storyRecordBuilder()
                        .id(1347)
                        .projectId(22)
                        .name("aStory2").build()
        );

    }
}
