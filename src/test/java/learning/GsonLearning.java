/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package learning;

import com.google.gson.Gson;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.test.module.utils.AdaptableGraphComponentTest;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import guru.bubl.module.model.graph.vertex.VertexPojo;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class GsonLearning extends AdaptableGraphComponentTest {
    @Test
    public void can_convert_vertex() throws Exception {
        Gson gson = new Gson();
        TagPojo timBernersLeePojo = modelTestScenarios.timBernersLee();
        vertexB.addTag(
                timBernersLeePojo
        );
        SubGraph graph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                3,
                ShareLevel.allShareLevelsInt
        );
        VertexPojo vertexBInSubGraphPojo = (VertexPojo) graph.vertexWithIdentifier(
                vertexB.uri()
        );
        String json = gson.toJson(vertexBInSubGraphPojo);
        System.out.println(
                json
        );
        JSONObject jsonObject = new JSONObject(json);
        VertexPojo vertexInSubGraphPojo = gson.fromJson(json, VertexPojo.class);
        System.out.println(vertexBInSubGraphPojo);
    }

    @Test
    public void map_converts_to_object_and_not_array() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("pomme", "avion");
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(
                gson.toJson(map)
        );
        assertThat(
                jsonObject.getString("pomme"),
                is("avion")
        );
        try {
            new JSONArray(
                    gson.toJson(map)
            );
            fail();
        } catch (Exception e) {
            //success
        }
    }

}
