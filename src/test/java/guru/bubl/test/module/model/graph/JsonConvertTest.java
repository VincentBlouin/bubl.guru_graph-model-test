/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.test.module.utils.ModelTestResources;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphJson;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JsonConvertTest extends ModelTestResources {

    @Test
    public void can_convert_vertex_to_and_from() {
        JSONObject vertexAJson = VertexInSubGraphJson.toJson(
                vertexInWholeConnectedGraph(vertexA)
        );
        VertexInSubGraphPojo vertexA = VertexInSubGraphJson.fromJson(
                vertexAJson
        );
        assertThat(
                vertexA.label(), is("vertex A")
        );
    }

//    @Test
//    @Ignore("included vertices feature suspsended")
//    public void can_convert_vertex_included_vertices_and_edges() {
//        VertexOperator newVertex = vertexFactory.createFromGraphElements(
//                vertexBAndC(),
//                edgeBetweenBAndCInSet()
//        );
//        newVertex.addRelationToVertex(vertexA);
//        JSONObject newVertexJson = VertexInSubGraphJson.toJson(
//                vertexInWholeConnectedGraph(
//                        newVertex
//                )
//        );
//        VertexInSubGraphPojo newVertexPojo = VertexInSubGraphJson.fromJson(
//                newVertexJson
//        );
//        assertThat(
//                newVertexPojo.getIncludedVertices().size(),
//                is(2)
//        );
//        assertThat(
//                newVertexPojo.getIncludedEdges().size(),
//                is(1)
//        );
//    }

    @Test
    public void converting_edge_to_json_throws_no_error() {
        EdgeJson.toJson(
                edgeInWholeGraph(
                        vertexA.getEdgeThatLinksToDestinationVertex(vertexB)
                )
        );
    }

    @Test
    public void can_convert_subgraph_to_and_from() {
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                10,
                ShareLevel.allShareLevelsInt
        );
        JSONObject subGraphJson = SubGraphJson.toJson(
                subGraph
        );
        subGraph = SubGraphJson.fromJson(
                subGraphJson
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
    }

    @Test
    public void subgraph_vertices_are_a_json_object_with_uri_as_key() throws Exception {
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                10,
                ShareLevel.allShareLevelsInt
        );
        JSONObject subGraphJson = SubGraphJson.toJson(
                subGraph
        );
        assertTrue(subGraphJson.has("vertices"));
        JSONObject vertices = subGraphJson.getJSONObject(
                "vertices"
        );
        JSONObject vertexAJson = vertices.getJSONObject(
                vertexA.uri().toString()
        );
        Vertex vertexAFromJson = VertexInSubGraphJson.fromJson(
                vertexAJson
        );
        assertThat(
                vertexAFromJson.label(),
                is("vertex A")
        );
    }

//    @Test
//    @Ignore ("included vertices is suspended feature")
//    public void included_vertices_are_a_json_object_mapped_with_uri() throws Exception {
//        VertexOperator newVertex = vertexFactory.createFromGraphElements(
//                vertexBAndC(),
//                edgeBetweenBAndCInSet()
//        );
//        newVertex.addRelationToVertex(vertexA);
//        JSONObject newVertexJson = VertexInSubGraphJson.toJson(
//                vertexInWholeConnectedGraph(newVertex)
//        );
//        JSONObject includedVertices = newVertexJson.getJSONObject(
//                "vertex"
//        ).getJSONObject("includedVertices");
//        Vertex vertexBFromJson = VertexInSubGraphJson.fromJson(
//                includedVertices.getJSONObject(
//                        vertexB.uri().toString()
//                ));
//        assertTrue(
//                vertexBFromJson.equals(vertexB)
//        );
//    }

    private Set<Vertex> vertexBAndC() {
        Set<Vertex> vertexBAndC = new HashSet<>();
        vertexBAndC.add(vertexB);
        vertexBAndC.add(vertexC);
        return vertexBAndC;
    }

    private Set<Edge> edgeBetweenBAndCInSet() {
        Set<Edge> edges = new HashSet<>();
        edges.add(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexC
                )
        );
        return edges;
    }
}

