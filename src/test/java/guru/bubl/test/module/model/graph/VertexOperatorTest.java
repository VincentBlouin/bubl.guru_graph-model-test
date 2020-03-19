/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.Triple;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.tag.TagFactory;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.suggestion.Suggestion;
import guru.bubl.module.model.suggestion.SuggestionPojo;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


public class VertexOperatorTest extends ModelTestResources {

    @Inject
    VertexFactory vertexFactory;

    @Inject
    TagFactory tagFactory;

    @Test
    public void can_update_label() {
        EdgeOperator newEdge = edgeFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        VertexOperator vertex = newEdge.destinationVertex();
        vertex.label("Ju-Ji-Tsu");
        assertThat(vertex.label(), is("Ju-Ji-Tsu"));
    }

    @Test 
    public void can_update_comment() {
        assertThat(
                vertexA.comment(),
                is("")
        );
        vertexA.comment(
                "Its vertex a !"
        );
        assertThat(
                vertexA.comment(),
                is("Its vertex a !")
        );
    }

    @Test 
    public void can_check_if_vertex_has_edge() {
        EdgeOperator edge = vertexA.getEdgeThatLinksToDestinationVertex(
                vertexB
        );
        assertTrue(
                vertexA.hasEdge(
                        edge
                )
        );
        assertTrue(
                vertexB.hasEdge(
                        edge
                )
        );
        edge.remove();
        assertFalse(
                vertexA.hasEdge(
                        edge
                )
        );
        assertFalse(
                vertexB.hasEdge(
                        edge
                )
        );
    }

    @Test 
    public void can_add_vertex_and_relation() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        Edge edge = vertexA.addVertexAndRelation();
        assertThat(edge, is(not(nullValue())));
        Integer newNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(newNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices + 2));
        assertTrue(vertexA.hasEdge(edge));

        assertThat(edge.sourceVertex().uri(), is(vertexA.uri()));

        Vertex destinationVertex = edge.destinationVertex();
        assertThat(destinationVertex, is(not(nullValue())));
    }

    @Test 
    public void can_remove_a_vertex() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();

        URI vertexBId = vertexB.uri();

        assertTrue(userGraph.haveElementWithId(vertexBId));
        vertexB.remove();
        assertFalse(userGraph.haveElementWithId(vertexBId));

        Integer updatedNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 3));
    }

    @Test 
    public void can_get_number_of_connected_edges() {
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
    }


    @Test 
    public void removing_a_vertex_removes_its_relations() {
        URI edgeBetweenCAndBUri = vertexB.getEdgeThatLinksToDestinationVertex(
                vertexC
        ).uri();
        URI edgeBetweenAAndBUri = vertexB.getEdgeThatLinksToDestinationVertex(
                vertexC
        ).uri();
        assertTrue(
                userGraph.haveElementWithId(
                        edgeBetweenCAndBUri
                )
        );
        assertTrue(
                userGraph.haveElementWithId(
                        edgeBetweenAAndBUri
                )
        );
        vertexB.remove();
        assertFalse(
                userGraph.haveElementWithId(
                        edgeBetweenCAndBUri
                )
        );
        assertFalse(
                userGraph.haveElementWithId(
                        edgeBetweenAAndBUri
                )
        );
    }


    @Test 
    public void can_add_suggestions_to_a_vertex() {
        assertTrue(
                vertexA.getSuggestions().isEmpty()
        );
        vertexA.addSuggestions(
                suggestionsToMap(
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user())
                )
        );
        assertFalse(
                vertexA.getSuggestions().isEmpty()
        );
        Suggestion addedSuggestion = vertexA.getSuggestions().values().iterator().next();
        assertThat(
                addedSuggestion.label(),
                is("Start date")
        );
        assertThat(
                addedSuggestion.getType().uri(),
                is(
                        URI.create("http://rdf.freebase.com/rdf/type/datetime")
                )
        );
        assertThat(
                addedSuggestion.getSameAs().uri(),
                is(
                        URI.create("http://rdf.freebase.com/rdf/time/event/start_date")
                )
        );
        assertTrue(
                addedSuggestion.origins().iterator().next().isRelatedToFriendlyResource(
                        new FriendlyResourcePojo(
                                URI.create("http://rdf.freebase.com/rdf/time/event")
                        )
                )
        );
    }

    @Test 
    public void edge_from_accepting_suggestion_has_suggestion_label() {
        SuggestionPojo nameSuggestion = modelTestScenarios.nameSuggestionFromPersonIdentification(
                user()
        );
        Edge edge = vertexA.acceptSuggestion(nameSuggestion);
        assertThat(edge.label(), is(nameSuggestion.label()));
    }

    @Test
    public void edge_from_accepting_suggestion_has_suggestion_same_as() {
        SuggestionPojo nameSuggestion = modelTestScenarios.nameSuggestionFromPersonIdentification(
                user()
        );
        Edge edge = vertexA.acceptSuggestion(nameSuggestion);
        Tag identification = edge.getTags().values().iterator().next();
        assertThat(
                identification.getExternalResourceUri(),
                Is.is(nameSuggestion.getSameAs().uri())
        );
    }

    @Test 
    public void new_vertex_from_accepting_suggestion_has_suggestion_same_as_and_type() {
        SuggestionPojo nameSuggestion = modelTestScenarios.nameSuggestionFromPersonIdentification(
                user()
        );
        Vertex newVertex = vertexA.acceptSuggestion(nameSuggestion).destinationVertex();
        assertThat(newVertex.getTags().size(), is(2));
        assertTrue(
                hasTypeWithExternalUri(
                        newVertex,
                        nameSuggestion.getSameAs().uri()
                )
        );
        assertTrue(
                hasTypeWithExternalUri(
                        newVertex,
                        nameSuggestion.getType().uri()
                )
        );
    }

    @Test 
    public void vertex_from_suggestion_from_comparison_has_type_label() {
        SuggestionPojo suggestion = testScenarios.suggestionFromComparisonForUserAndTriple(
                anotherUser,
                Triple.fromEdgeSourceAndDestination(
                        vertexA.getEdgeThatLinksToDestinationVertex(vertexB),
                        vertexOfAnotherUser,
                        vertexB
                )
        );
        Vertex newVertex = vertexOfAnotherUser.acceptSuggestion(
                suggestion
        ).destinationVertex();
        assertThat(
                newVertex.label(),
                is("vertex B")
        );
    }

    @Test 
    public void vertex_from_suggestion_from_comparison_is_not_identified_to_suggestion_same_as() {
        SuggestionPojo suggestion = testScenarios.suggestionFromComparisonForUserAndTriple(
                anotherUser,
                Triple.fromEdgeSourceAndDestination(
                        vertexA.getEdgeThatLinksToDestinationVertex(vertexB),
                        vertexOfAnotherUser,
                        vertexB
                )
        );
        VertexOperator newVertex = vertexOfAnotherUser.acceptSuggestion(
                suggestion
        ).destinationVertex();
        assertFalse(
                newVertex.getTags().containsKey(
                        suggestion.getSameAs().uri()
                )
        );
    }

    @Test 
    public void can_get_empty_list_after_removing_last_same_as() {
        Tag timBernersLee = vertexA.addTag(
                modelTestScenarios.timBernersLee()
        ).values().iterator().next();
        assertFalse(vertexA.getTags().isEmpty());
        vertexA.removeTag(
                timBernersLee
        );
        assertTrue(vertexA.getTags().isEmpty());
    }

    @Test 
    public void deleting_a_vertex_does_not_delete_its_identifications_in_the_graph() {
        assertTrue(
                userGraph.haveElementWithId(
                        vertexB.uri()
                )
        );
        vertexA.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexB
                )
        );
        vertexA.remove();
        assertTrue(
                userGraph.haveElementWithId(
                        vertexB.uri()
                )
        );
    }

    @Test 
    public void can_assign_the_same_identification_to_2_vertices() {
        Tag timBernersLee = vertexA.addTag(
                modelTestScenarios.timBernersLee()
        ).values().iterator().next();
        vertexB.addTag(
                modelTestScenarios.timBernersLee()
        );
        assertTrue(
                vertexA.getTags().containsKey(
                        timBernersLee.getExternalResourceUri()
                )
        );
        assertTrue(
                vertexB.getTags().containsKey(
                        timBernersLee.getExternalResourceUri()
                )
        );
    }

    @Test 
    public void can_get_same_as() {
        EdgeOperator newEdge = edgeFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        VertexOperator newVertex = newEdge.destinationVertex();
        newVertex.label("Tim Berners Lee");
        assertTrue(newVertex.getTags().isEmpty());
        newVertex.addTag(modelTestScenarios.timBernersLee());
        Tag sameAs = newVertex.getTags().values().iterator().next();
        assertThat(
                sameAs.getExternalResourceUri(),
                is(
                        modelTestScenarios.timBernersLee().getExternalResourceUri()
                )
        );
    }

    @Test 
    public void can_add_generic_identification() {
        assertFalse(vertexA.getTags().containsKey(
                modelTestScenarios.extraterrestrial().getExternalResourceUri()
        ));
        Tag ExtraTerrestrial = vertexA.addTag(
                modelTestScenarios.extraterrestrial()
        ).values().iterator().next();
        assertTrue(vertexA.getTags().containsKey(
                ExtraTerrestrial.getExternalResourceUri()
        ));
    }

    @Test 
    public void can_test_if_vertex_has_destination_vertex() {
        assertFalse(vertexA.hasDestinationVertex(vertexC));
        vertexA.addRelationToVertex(vertexC);
        assertTrue(vertexA.hasDestinationVertex(vertexC));
    }

    @Test 
    public void source_vertex_is_not_a_destination_vertex() {
        vertexA.addRelationToVertex(vertexC);
        assertTrue(vertexA.hasDestinationVertex(vertexC));
        assertFalse(vertexC.hasDestinationVertex(vertexA));
    }

    @Test 
    public void there_is_a_creation_date() {
        assertThat(
                vertexA.creationDate(),
                is(not(nullValue()))
        );
    }

    @Test 
    public void there_is_a_last_modification_date() {
        assertThat(
                vertexA.lastModificationDate(),
                is(not(nullValue()))
        );
    }

    @Test 
    public void a_vertex_is_private_by_default() {
        VertexOperator newVertex = vertexFactory.withUri(
                vertexA.addVertexAndRelation().destinationVertex().uri()
        );
        assertFalse(newVertex.isPublic());
    }


    @Test 
    public void can_make_private_vertex_with_no_relations() {
        VertexOperator noRelationsVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        noRelationsVertex.makePublic();
        assertTrue(noRelationsVertex.isPublic());
        noRelationsVertex.makePrivate();
        assertFalse(noRelationsVertex.isPublic());
    }

    @Test 
    public void making_a_vertex_public_makes_all_its_edges_public_where_the_other_end_vertex_is_also_public() {
        vertexA.setShareLevel(ShareLevel.PUBLIC);
        vertexC.setShareLevel(ShareLevel.PRIVATE);
        vertexB.setShareLevel(ShareLevel.PRIVATE);
        assertFalse(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexC
                ).isPublic()
        );
        vertexB.makePublic();
        assertTrue(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexC
                ).isPublic()
        );
    }

    @Test 
    public void making_a_vertex_private_makes_all_its_edges_private() {
        vertexA.makePublic();
        vertexB.makePublic();
        vertexC.makePrivate();
        assertTrue(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexC
                ).isPublic()
        );
        vertexB.makePrivate();
        assertFalse(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexC
                ).isPublic()
        );
    }


    @Test 
    public void can_add_vertex_and_relation_using_edge_and_vertex_uris() {
        UUID vertexId = UUID.randomUUID();
        UUID edgeId = UUID.randomUUID();
        EdgePojo newEdge = vertexA.addVertexAndRelationWithIds(
                vertexId.toString(),
                edgeId.toString()
        );
        UserUris userUris = new UserUris(vertexA.getOwnerUsername());
        URI vertexUri = userUris.vertexUriFromShortId(vertexId.toString());
        assertThat(
                newEdge.destinationVertex().uri(),
                is(vertexUri)
        );
        URI edgeUri = userUris.edgeUriFromShortId(edgeId.toString());
        assertThat(
                newEdge.uri(),
                is(edgeUri)
        );
    }

    @Test 
    public void checks_that_uris_dont_already_exist() {
        UUID vertexId = UUID.fromString(
                UserUris.graphElementShortId(vertexB.uri())
        );
        UUID edgeId = UUID.fromString(
                UserUris.graphElementShortId(vertexA.getEdgeThatLinksToDestinationVertex(vertexB).uri())
        );
        EdgePojo newEdge = vertexA.addVertexAndRelationWithIds(
                vertexId.toString(),
                edgeId.toString()
        );
        UserUris userUris = new UserUris(vertexA.getOwnerUsername());
        URI vertexUri = userUris.vertexUriFromShortId(vertexId.toString());
        assertThat(
                newEdge.destinationVertex().uri(),
                is(not(vertexUri))
        );
        URI edgeUri = userUris.edgeUriFromShortId(edgeId.toString());
        assertThat(
                newEdge.uri(),
                is(not(edgeUri))
        );
    }

    @Test 
    public void changing_share_level_from_friend_to_public_decrements_neighbors_number_of_friends() {
        vertexB.setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(1)
        );
        vertexB.setShareLevel(ShareLevel.PUBLIC);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
    }

    @Test 
    public void can_check_equality() {
        assertTrue(vertexA.equals(vertexA));
        assertFalse(vertexA.equals(vertexB));
    }

    @Test 
    public void can_compare_to_friendly_resource() {
        FriendlyResource vertexAAsFriendlyResource = vertexA;
        assertTrue(vertexA.equals(vertexAAsFriendlyResource));
    }

    @Test 
    public void can_remove_a_vertex_having_no_relations() {
        VertexOperator vertexWithNoRelations = vertexFactory.createForOwner(
                user().username()
        );
        URI vertexWithNoRelationsUri = vertexWithNoRelations.uri();
        assertTrue(
                userGraph.haveElementWithId(
                        vertexWithNoRelationsUri
                )
        );
        vertexWithNoRelations.remove();
        assertFalse(
                userGraph.haveElementWithId(
                        vertexWithNoRelationsUri
                )
        );
    }

    @Test 
    public void adding_a_relation_to_existing_vertices_increments_number_of_connected_edges() {
        int nbEdgesForVertexA = vertexA.getNbNeighbors().getTotal();
        int nbEdgesForVertexC = vertexC.getNbNeighbors().getTotal();
        vertexC.addRelationToVertex(vertexA);
        assertThat(
                vertexA.getNbNeighbors().getTotal(),
                is(nbEdgesForVertexA + 1)
        );
        assertThat(
                vertexC.getNbNeighbors().getTotal(),
                is(nbEdgesForVertexC + 1)
        );
    }

    @Test 


    public void adding_a_relation_to_existing_vertices_does_not_increment_nb_public_neighbors_if_both_are_private() {
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexC.addRelationToVertex(vertexA);
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test 


    public void adding_a_relation_to_existing_vertices_increments_nb_public_neighbors_to_source_if_destination_is_public() {
        vertexA.makePublic();
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexC.addRelationToVertex(vertexA);
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(1)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test 


    public void adding_a_relation_to_existing_vertices_increments_nb_public_neighbors_to_destination_if_source_is_public() {
        vertexC.makePublic();
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexC.addRelationToVertex(vertexA);
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
    }

    @Test 


    public void adding_a_relation_to_existing_vertices_increments_nb_friend_neighbors_to_source_if_destination_is_friend() {
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(0)
        );
        vertexC.addRelationToVertex(vertexA);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(1)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(0)
        );
    }

    @Test 


    public void adding_a_relation_to_existing_vertices_increments_nb_friend_neighbors_to_destination_if_source_is_friend() {
        vertexC.setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(0)
        );
        vertexC.addRelationToVertex(vertexA);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(1)
        );
    }

    @Test 


    public void fork_does_not_have_the_same_uri() {
        Vertex vertexAClone = vertexA.forkForUserUsingCache(
                user,
                vertexA
        );
        assertThat(
                vertexAClone.uri(),
                not(vertexA.uri())
        );
    }

    @Test 


    public void fork_has_same_label_and_comment() {
        vertexA.comment(
                "vertex A comment"
        );
        VertexOperator vertexAClone = vertexA.forkForUserUsingCache(
                user,
                vertexA
        );
        assertThat(
                vertexAClone.label(),
                is("vertex A")
        );
        assertThat(
                vertexAClone.comment(),
                is("vertex A comment")
        );
    }

    @Test 


    public void fork_is_identified_to_original_vertex() {
        VertexOperator vertexAClone = vertexA.forkForUserUsingCache(
                user,
                vertexA
        );
        vertexAClone.getTags().containsKey(
                vertexA.uri()
        );
    }

    @Test 


    public void fork_identification_to_original_vertex_has_the_original_vertex_label() {
        VertexOperator vertexAClone = vertexA.forkForUserUsingCache(
                user,
                vertexA
        );
        Tag identification = tagFactory.withUri(
                vertexAClone.getTags().get(
                        vertexA.uri()
                ).uri()
        );
        assertThat(
                identification.label(),
                is("vertex A")
        );
    }

    @Test 


    public void making_vertex_public_increments_the_number_of_public_neighbor_vertices_set_to_neighbors() {
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexB.makePublic();
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(1)
        );
    }

    @Test 


    public void making_vertex_private_decrements_the_number_of_public_neighbor_vertices_set_to_neighbors() {
        vertexB.makePublic();
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(1)
        );
        vertexB.makePrivate();
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test 


    public void new_child_vertex_nb_public_neighbors_is_set_to_1_when_parent_is_public() {
        VertexOperator newVertex = vertexFactory.withUri(
                vertexA.addVertexAndRelation().destinationVertex().uri()
        );
        assertThat(
                newVertex.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexA.makePublic();
        VertexOperator anotherNewVertex = vertexFactory.withUri(
                vertexA.addVertexAndRelation().destinationVertex().uri()
        );
        assertThat(
                anotherNewVertex.getNbNeighbors().getPublic(),
                is(1)
        );
    }


    @Test 
    public void mergeTo_removes_vertex() {
        URI farCenterVertexUri = userGraph.createVertex().uri();
        VertexOperator farCenterVertex = vertexFactory.withUri(
                farCenterVertexUri
        );
        assertTrue(userGraph.haveElementWithId(farCenterVertexUri));
        farCenterVertex.mergeTo(vertexC);
        assertFalse(userGraph.haveElementWithId(farCenterVertexUri));
    }

    @Test 
    public void mergeTo_includes_edges() {
        URI farCenterVertexUri = userGraph.createVertex().uri();
        VertexOperator farCenterVertex = vertexFactory.withUri(
                farCenterVertexUri
        );
        Edge edge1 = farCenterVertex.addVertexAndRelation();
        Edge edge2 = farCenterVertex.addVertexAndRelation();
        assertFalse(
                vertexC.hasEdge(edge1)
        );
        assertFalse(
                vertexC.hasEdge(edge2)
        );
        farCenterVertex.mergeTo(vertexC);
        assertTrue(
                vertexC.hasEdge(edge1)
        );
        assertTrue(
                vertexC.hasEdge(edge2)
        );
    }

    @Test 


    public void mergeTo_preserves_edges_direction() {
        URI farCenterVertexUri = userGraph.createVertex().uri();
        VertexOperator farCenterVertex = vertexFactory.withUri(
                farCenterVertexUri
        );
        Edge edge1 = farCenterVertex.addVertexAndRelation();
        Vertex edge1OtherVertex = edge1.destinationVertex();
        edgeFactory.withUri(edge1.uri()).inverse();
        farCenterVertex.mergeTo(vertexC);

        EdgeOperator edge1Operator = vertexC.connectedEdges().get(
                edge1.uri()
        );
        assertThat(
                edge1Operator.sourceVertex(),
                is(edge1OtherVertex)
        );
        assertThat(
                edge1Operator.destinationVertex(),
                is(vertexC)
        );
    }

    @Test 


    public void mergeTo_keeps_tags() {
        vertexA.addTag(modelTestScenarios.computerScientistType());
        vertexC.addTag(modelTestScenarios.human());
        assertThat(
                vertexA.getTags().size(),
                is(1)
        );
        vertexC.mergeTo(vertexA);
        assertThat(
                vertexA.getTags().size(),
                is(2)
        );
    }

    @Test 

    public void cannot_merge_to_another_vertex_when_being_under_a_pattern() {
        vertexA.makePattern();
        VertexOperator farVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        farVertex.addVertexAndRelation();
        Boolean success = vertexB.mergeTo(farVertex);
        assertFalse(
                success
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                farVertex.getNbNeighbors().getTotal(),
                is(1)
        );
    }

    @Test 

    public void cannot_merge_to_another_vertex_when_being_a_pattern() {
        vertexA.makePattern();
        VertexOperator farVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        farVertex.addVertexAndRelation();
        Boolean success = vertexA.mergeTo(farVertex);
        assertFalse(
                success
        );
        assertThat(
                vertexA.getNbNeighbors().getTotal(),
                is(1)
        );
        assertThat(
                farVertex.getNbNeighbors().getTotal(),
                is(1)
        );
    }

    @Test 

    public void cannot_merge_to_a_far_vertex_under_a_pattern() {
        VertexOperator farVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        VertexOperator childOfFarVertex = vertexFactory.withUri(
                farVertex.addVertexAndRelation().destinationVertex().uri()
        );
        farVertex.makePattern();
        assertTrue(childOfFarVertex.isUnderPattern());
        Boolean success = vertexB.mergeTo(childOfFarVertex);
        assertFalse(
                success
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                farVertex.getNbNeighbors().getTotal(),
                is(1)
        );
    }

    @Test 

    public void cannot_merge_to_a_far_vertex_that_is_a_pattern() {
        VertexOperator farVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        VertexOperator childOfFarVertex = vertexFactory.withUri(
                farVertex.addVertexAndRelation().destinationVertex().uri()
        );
        farVertex.makePattern();
        Boolean success = vertexB.mergeTo(farVertex);
        assertFalse(
                success
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                farVertex.getNbNeighbors().getTotal(),
                is(1)
        );
    }

    @Test 

    public void can_set_share_level() {
        assertThat(
                vertexA.getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexA.setShareLevel(
                ShareLevel.FRIENDS
        );
        assertThat(
                vertexA.getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
    }

    @Test 

    public void setting_share_level_sets_it_for_surrounding_edges() {
        assertThat(
                vertexB.getEdgeThatLinksToDestinationVertex(vertexC).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexB.setShareLevel(
                ShareLevel.FRIENDS
        );
        assertThat(
                vertexB.getEdgeThatLinksToDestinationVertex(vertexC).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexC.setShareLevel(
                ShareLevel.FRIENDS
        );
        assertThat(
                vertexB.getEdgeThatLinksToDestinationVertex(vertexC).getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
        vertexB.setShareLevel(ShareLevel.PUBLIC);

        assertThat(
                vertexB.getEdgeThatLinksToDestinationVertex(vertexC).getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
    }

    @Test 

    public void setting_share_level_to_friends_increments_number_of_friend_neighbors_to_neighbors() {
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
        vertexB.setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(1)
        );
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(1)
        );
    }

    @Test 
    public void make_pattern_makes_connected_nodes_public() {
        assertFalse(vertexC.isPublic());
        vertexA.makePattern();
        assertTrue(vertexC.isPublic());
    }

    @Test
    public void make_pattern_updates_nb_vertices_of_private_nb_to_public() {
        vertexC.setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                vertexB.getNbNeighbors().getFriend(),
                is(1)
        );
        assertThat(
                vertexB.getNbNeighbors().getPrivate(),
                is(2)
        );
        assertThat(
                vertexB.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexA.makePattern();
        assertThat(
                vertexB.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexB.getNbNeighbors().getPrivate(),
                is(0)
        );
        assertThat(
                vertexB.getNbNeighbors().getPublic(),
                is(2)
        );
    }

    @Test 

    public void make_pattern_makes_tags_public() {
        TagPojo tag = vertexB.addTag(
                modelTestScenarios.location()
        ).values().iterator().next();
        assertThat(
                tagFactory.withUri(
                        tag.uri()
                ).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexA.makePattern();
        assertThat(
                tagFactory.withUri(
                        tag.uri()
                ).getShareLevel(),
                is(ShareLevel.PUBLIC)
        );
    }

    @Test 

    public void make_pattern_makes_tags_of_edges_public() {
        TagPojo tag = edgeFactory.withUri(
                vertexB.getEdgeThatLinksToDestinationVertex(vertexC).uri()
        ).addTag(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        assertThat(
                tagFactory.withUri(
                        tag.uri()
                ).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexA.makePattern();
        assertThat(
                tagFactory.withUri(
                        tag.uri()
                ).getShareLevel(),
                is(ShareLevel.PUBLIC)
        );
    }

    @Test 

    public void make_pattern_does_not_make_public_a_vertex_that_shares_a_tag_with_a_connected_vertex() {
        vertexC.getEdgeThatLinksToDestinationVertex(vertexB).remove();
        vertexC.addTag(modelTestScenarios.computerScientistType());
        vertexB.addTag(modelTestScenarios.computerScientistType());
        assertFalse(vertexC.isPublic());
        vertexA.makePattern();
        assertFalse(vertexC.isPublic());
    }

    @Test 

    public void sets_flag_on_edges_and_vertices_under_a_pattern() {
        assertFalse(vertexA.isUnderPattern());
        assertFalse(vertexA.getEdgeThatLinksToDestinationVertex(vertexB).isUnderPattern());
        assertFalse(vertexB.isUnderPattern());
        assertFalse(vertexB.getEdgeThatLinksToDestinationVertex(vertexC).isUnderPattern());
        assertFalse(vertexC.isUnderPattern());
        vertexA.makePattern();
        assertFalse(vertexA.isUnderPattern());
        assertTrue(vertexA.getEdgeThatLinksToDestinationVertex(vertexB).isUnderPattern());
        assertTrue(vertexB.isUnderPattern());
        assertTrue(vertexB.getEdgeThatLinksToDestinationVertex(vertexC).isUnderPattern());
        assertTrue(vertexC.isUnderPattern());
    }

    @Test 

    public void cannot_make_pattern_out_of_a_vertex_under_a_pattern() {
        Boolean success = vertexA.makePattern();
        assertTrue(
                success
        );
        success = vertexB.makePattern();
        assertFalse(
                success
        );
        assertFalse(
                vertexB.isPattern()
        );
    }

    @Test 
    public void undo_pattern_removes_flag_to_children() {
        vertexB.makePattern();
        vertexB.undoPattern();
        assertFalse(
                vertexA.isUnderPattern()
        );
        assertFalse(
                vertexC.isUnderPattern()
        );
    }

    @Test 
    public void create_vertex_and_relation_on_under_pattern_vertex_have_flag_under_pattern() {
        vertexA.makePattern();
        EdgePojo edge = vertexA.addVertexAndRelationWithIds(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        assertTrue(
                edgeFactory.withUri(
                        edge.uri()
                ).isUnderPattern()
        );
        assertTrue(
                vertexFactory.withUri(
                        edge.destinationVertex().uri()
                ).isUnderPattern()
        );
        assertTrue(
                vertexFactory.withUri(
                        edge.destinationVertex().uri()
                ).isPublic()
        );
    }

    @Test 
    public void cannot_add_relation_to_distant_vertex_when_being_a_pattern_or_under_a_pattern() {
        VertexOperator farVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        vertexB.makePattern();
        assertThat(
                vertexB.addRelationToVertex(farVertex),
                is(nullValue())
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                vertexC.addRelationToVertex(farVertex),
                is(nullValue())
        );
        assertThat(
                vertexC.getNbNeighbors().getTotal(),
                is(1)
        );
    }


    @Test 
    public void cannot_add_relation_to_distant_vertex_that_is_a_pattern_or_under_a_pattern() {
        VertexOperator farVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        VertexOperator farChildVertex = vertexFactory.withUri(
                farVertex.addVertexAndRelation().destinationVertex().uri()
        );
        farVertex.makePattern();
        assertThat(
                vertexB.addRelationToVertex(farChildVertex),
                is(nullValue())
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                vertexB.addRelationToVertex(farVertex),
                is(nullValue())
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
    }

    @Test 
    public void on_pattern_create_nb_pattern_usage_is_zero() {
        vertexA.makePattern();
        assertThat(
                vertexA.getNbPatternUsage(),
                is(0)
        );
    }

    @Test 
    public void undo_pattern_and_remake_resets_number_of_pattern_usage() {
        vertexA.makePattern();
        patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexA.uri()
        ).use();
        vertexA.undoPattern();
        vertexA.makePattern();
        assertThat(
                vertexA.getNbPatternUsage(),
                is(0)
        );
    }


    private Boolean hasTypeWithExternalUri(Vertex vertex, URI externalUri) {
        for (Tag identification : vertex.getTags().values()) {
            if (identification.getExternalResourceUri().equals(externalUri)) {
                return true;
            }
        }
        return false;
    }
}

