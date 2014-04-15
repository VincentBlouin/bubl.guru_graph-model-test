package org.triple_brain.module.model.graph;

import com.google.common.collect.ImmutableSet;
import org.junit.Ignore;
import org.junit.Test;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.Image;
import org.triple_brain.module.model.test.SubGraphOperator;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.model.graph.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.vertex.*;
import org.triple_brain.module.model.suggestion.*;

import javax.inject.Inject;
import java.net.URI;
import java.util.*;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class UserGraphTest extends AdaptableGraphComponentTest {
    public static final int DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    protected FriendlyResourceFactory friendlyResourceFactory;

    @Test
    public void can_get_graph_with_default_center_vertex() {
        SubGraph graph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(graph.numberOfEdges(), is(2));
        assertTrue(graph.containsVertex(vertexA));
    }

    @Test
    public void can_get_graph_with_custom_center_vertex() {
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                vertexB.uri());
        assertThat(graph, is(not(nullValue())));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexB.uri());
        assertThat(graph.numberOfEdges(), is(2));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(centerVertex.label(), is("vertex B"));
    }

    @Test
    public void correct_edges_are_in_graph() {
        Edge betweenAAndB = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        Edge betweenBAndC = vertexB.edgeThatLinksToDestinationVertex(vertexC);
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        assertThat(
                subGraph.edges().size(),
                is(2)
        );
        assertTrue(
                subGraph.containsEdge(betweenAAndB)
        );
        assertTrue(
                subGraph.containsEdge(betweenBAndC)
        );
    }

    @Test
    public void source_and_destination_vertex_are_in_edges() {
        Edge betweenAAndB = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        Edge betweenAAndBFromSubGraph = subGraph.edgeWithIdentifier(
                betweenAAndB.uri()
        );
        assertTrue(
                betweenAAndBFromSubGraph.sourceVertex().equals(
                        vertexA
                )
        );
        assertTrue(
                betweenAAndBFromSubGraph.destinationVertex().equals(
                        vertexB
                )
        );
    }

    @Test
    public void has_generic_identifications(){
        vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getGenericIdentifications().values().iterator().hasNext()
        );
    }

    @Test
    public void has_same_as(){
        vertexA.addSameAs(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getSameAs().values().iterator().hasNext()
        );
    }

    @Test
    public void has_types(){
        vertexA.addType(
                modelTestScenarios.personType()
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getAdditionalTypes().entrySet().iterator().hasNext()
        );
        FriendlyResource additionalType = vertexAInSubGraph.getAdditionalTypes().values().iterator().next();
        assertThat(additionalType.label(), is("Person"));
    }

    @Test
    public void vertex_suggestions_have_their_properties_sub_graph() {
        Set<SuggestionPojo> suggestions = new HashSet<>(
                Arrays.asList(
                        modelTestScenarios.startDateSuggestionFromEventIdentification()
                )
        );
        vertexA.addSuggestions(
                suggestions
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        Suggestion suggestion = vertexAInSubGraph.suggestions().values().iterator().next();
        assertThat(suggestion.label(), is("Start date"));
    }
    @Test
    public void suggestions_have_their_own_label() {
        Set<SuggestionPojo> suggestions = new HashSet<>(
                Arrays.asList(
                        modelTestScenarios.startDateSuggestionFromEventIdentification(),
                        modelTestScenarios.nameSuggestionFromPersonIdentification()
                )
        );
        vertexA.addSuggestions(
                suggestions
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        List<String> labels = new ArrayList<>();
        for(Suggestion suggestion : vertexAInSubGraph.suggestions().values()){
            labels.add(suggestion.label());
        }
        assertTrue(labels.contains("Start date"));
        assertTrue(labels.contains("Name"));
    }

    @Test
    public void has_suggestions_origin() {
        Set<SuggestionPojo> suggestions = new HashSet<>(
                Arrays.asList(
                        modelTestScenarios.startDateSuggestionFromEventIdentification()
                )
        );
        vertexA.addSuggestions(
                suggestions
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        Suggestion suggestion = vertexAInSubGraph.suggestions().values().iterator().next();
        SuggestionOrigin origin = suggestion.origins().iterator().next();
        FriendlyResourcePojo identification = new FriendlyResourcePojo(
                URI.create("http://rdf.freebase.com/rdf/time/event")
        );
        assertTrue(
                origin.isRelatedToFriendlyResource(
                        identification
                )
        );
    }

    @Test
    @Ignore("to complete")
    public void has_suggestion_multiple_origins() {
        vertexA.addSuggestions(
                new HashSet<>(
                        Arrays.asList(
                                modelTestScenarios.nameSuggestionFromPersonIdentification()
                        )
                )
        );
        vertexA.addSuggestions(
                new HashSet<>(
                        Arrays.asList(
                                modelTestScenarios.nameSuggestionFromSymbolIdentification()
                        )
                )
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        Suggestion suggestionInSubGraph = vertexAInSubGraph.suggestions().values().iterator().next();
        assertThat(
                suggestionInSubGraph.origins().size(),
                is(2)
        );
    }

    @Test
    public void can_get_multiple_suggestions_in_sub_graph() {
        Set<SuggestionPojo> suggestions = new HashSet<>(
                Arrays.asList(
                        modelTestScenarios.startDateSuggestionFromEventIdentification(),
                        modelTestScenarios.nameSuggestionFromPersonIdentification()
                )
        );
        vertexA.addSuggestions(
                suggestions
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAInSubGraph.suggestions().size(),
                is(2)
        );
    }

    @Test
    public void has_included_vertices_and_edges(){
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );
        newVertex.addRelationToVertex(vertexA);
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph compositeVertexInSubGraph = subGraph.vertexWithIdentifier(
                newVertex.uri()
        );
        assertThat(
                compositeVertexInSubGraph.getIncludedVertices().size(),
                is(2)
        );
        assertThat(
                compositeVertexInSubGraph.getIncludedEdges().size(),
                is(1)
        );
    }

    @Test
    public void included_edges_have_source_and_destination_vertices(){
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );
        newVertex.addRelationToVertex(vertexA);
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        Edge edge = subGraph.vertexWithIdentifier(
                newVertex.uri()
        ).getIncludedEdges().values().iterator().next();
        assertNotNull(
                edge.sourceVertex()
        );
        assertNotNull(
                edge.destinationVertex()
        );
    }

    @Test
    public void has_vertices_images(){
        Image image1 = Image.withUriForSmallAndBigger(
                URI.create("/small_1"),
                URI.create("/large_1")
        );
        Image image2 = Image.withUriForSmallAndBigger(
                URI.create("/small_2"),
                URI.create("/large_2")
        );
        Set<Image> images = ImmutableSet.of(
                image1,
                image2
        );
        vertexA.addImages(images);
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAInSubGraph.images().size(),
                is(2)
        );
        assertTrue(
                vertexAInSubGraph.images().contains(image1)
        );
        assertTrue(
                vertexAInSubGraph.images().contains(image2)
        );
    }

    @Test
    public void has_identification_images(){
        Image image1 = Image.withUriForSmallAndBigger(
                URI.create("/small_1"),
                URI.create("/large_1")
        );
        Image image2 = Image.withUriForSmallAndBigger(
                URI.create("/small_2"),
                URI.create("/large_2")
        );
        Set<Image> images = ImmutableSet.of(
                image1,
                image2
        );
        FriendlyResourceOperator friendlyResourceOperator = friendlyResourceFactory.withUri(
                modelTestScenarios.computerScientistType().uri()
        );
        vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        friendlyResourceOperator.addImages(
                images
        );
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        FriendlyResource identificationInSubGraph = vertexAInSubGraph.getGenericIdentifications().values().iterator().next();
        assertThat(
                identificationInSubGraph.images().size(),
                is(2)
        );
        assertTrue(
                identificationInSubGraph.images().contains(image1)
        );
        assertTrue(
                identificationInSubGraph.images().contains(image2)
        );
    }


    @Test
    public void can_get_circular_graph_with_default_center_vertex() {
        vertexC.addRelationToVertex(vertexA);
        SubGraph graph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices()));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexA.uri());
        assertThat(centerVertex.label(), is("vertex A"));
    }

    @Test
    public void can_get_a_limited_graph_with_default_center_vertex() throws Exception {
        SubGraph subGraph = userGraph.graphWithDefaultVertexAndDepth(2);
        assertThat(subGraph.numberOfEdges(), is(2));
        assertThat(subGraph.numberOfVertices(), is(3));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDefaultVertexAndDepth(1);
        assertThat(subGraph.numberOfEdges(), is(1));
        assertThat(subGraph.numberOfVertices(), is(2));
        assertFalse(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(vertexA));
    }

    @Test

    public void can_get_a_limited_graph_with_a_custom_center_vertex() {
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                vertexC.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(2));
        assertThat(subGraph.numberOfEdges(), is(1));
        assertFalse(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test

    public void can_get_sub_graph_of_destination_vertex_of_center_vertex() {
        Edge newEdge = vertexC.addVertexAndRelation();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2, vertexB.uri()
        );
        assertThat(subGraph.numberOfEdges(), is(3));
        assertThat(subGraph.numberOfVertices(), is(4));

        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex() {
        SubGraph subGraph;
        Edge newEdge = vertexA.addVertexAndRelation();
        subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2, vertexB.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(4));
        assertThat(subGraph.numberOfEdges(), is(3));

        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex_having_also_a_circular_relation() {
        vertexC.addRelationToVertex(vertexA);
        Edge edgeGoingOutOfC = vertexC.addVertexAndRelation();

        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2,
                vertexA.uri()
        );
        assertTrue(subGraph.containsVertex(edgeGoingOutOfC.destinationVertex()));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        SubGraph subGraph = userGraph.graphWithDefaultVertexAndDepth(0);
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDepthAndCenterVertexId(
                0, vertexB.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexB));
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_default_center_vertex_with_negative_depth() {
        try {
            userGraph.graphWithDefaultVertexAndDepth(-1);
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex uri was:" + vertexA.uri()));
        }
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_custom_center_vertex_with_negative_depth() {
        try {
            userGraph.graphWithDepthAndCenterVertexId(-1, vertexB.uri());
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex uri was:" + vertexB.uri()));
        }
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_non_existing_center_vertex() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        try {
            userGraph.graphWithDepthAndCenterVertexId(1, Uris.get("/invalid_uri"));
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: /invalid_uri not found"));
        }
        assertThat(numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    @Ignore("to implement later, not a priority")
    public void can_get_rdf_xml_representation_of_graph() {
        assertThat(userGraph.toRdfXml(), is(not(nullValue())));
    }

    @Test
    public void distance_from_center_vertex_is_set_for_each_vertex_in_sub_graph() {
        assertThat(
                vertexInWholeConnectedGraph(vertexA).minDistanceFromCenterVertex(),
                is(0)
        );
        assertThat(
                vertexInWholeConnectedGraph(vertexB).minDistanceFromCenterVertex(),
                is(1)
        );
        assertThat(
                vertexInWholeConnectedGraph(vertexC).minDistanceFromCenterVertex(),
                is(2)
        );
    }

    @Test
    public void the_minimum_distance_from_center_vertex_is_returned() {
        assertThat(
                vertexInWholeConnectedGraph(vertexC).minDistanceFromCenterVertex(),
                is(2)
        );
        vertexC().addRelationToVertex(vertexA());
        assertThat(
                vertexInWholeConnectedGraph(vertexC).minDistanceFromCenterVertex(),
                is(1)
        );
    }

    @Test
    public void can_create_new_vertex_out_of_nothing() {
        Vertex vertex = userGraph.createVertex();
        SubGraphOperator subGraph = wholeGraph();
        assertTrue(subGraph.containsVertex(vertex));
    }

    @Override
    public VertexInSubGraphPojo vertexInWholeConnectedGraph(Vertex vertex) {
        return (VertexInSubGraphPojo) userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        ).vertexWithIdentifier(vertex.uri());
    }

    private Set<Vertex> vertexBAndC() {
        Set<Vertex> vertexBAndC = new HashSet<>();
        vertexBAndC.add(vertexB);
        vertexBAndC.add(vertexC);
        return vertexBAndC;
    }

    private Set<Edge> edgeBetweenBAndCInSet() {
        Set<Edge> edges = new HashSet<>();
        edges.add(
                vertexB.edgeThatLinksToDestinationVertex(
                        vertexC
                )
        );
        return edges;
    }

}
