/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.relation.RelationPojo;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagFactory;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
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
        RelationOperator newEdge = relationFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        VertexOperator vertex = vertexFactory.withUri(newEdge.destinationUri());
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
        RelationOperator edge = vertexA.getEdgeToDestinationVertex(
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
        Relation relation = vertexA.addVertexAndRelation();
        assertThat(relation, is(not(nullValue())));
        Integer newNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(newNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices + 2));
        assertTrue(vertexA.hasEdge(relation));

        assertThat(relation.sourceUri(), is(vertexA.uri()));

        Vertex destinationVertex = vertexFactory.withUri(relation.destinationUri());
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
        URI edgeBetweenCAndBUri = vertexB.getEdgeToDestinationVertex(
                vertexC
        ).uri();
        URI edgeBetweenAAndBUri = vertexB.getEdgeToDestinationVertex(
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
    public void remove_vertex_prevents_remove_surround_group_relations() {
        vertexC.remove();
        assertTrue(userGraph.haveElementWithId(groupRelation.uri()));
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
        RelationOperator newEdge = relationFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        VertexOperator newVertex = vertexFactory.withUri(newEdge.destinationUri());
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
        vertexA.addRelationToFork(
                vertexC.uri(),
                vertexA.getShareLevel(),
                vertexC.getShareLevel()
        );
        assertTrue(vertexA.hasDestinationVertex(vertexC));
    }

    @Test
    public void source_vertex_is_not_a_destination_vertex() {
        vertexA.addRelationToFork(
                vertexC.uri(),
                vertexA.getShareLevel(),
                vertexC.getShareLevel()
        );
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
                vertexA.addVertexAndRelation().destinationUri()
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
                vertexB.getEdgeToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeToDestinationVertex(
                        vertexC
                ).isPublic()
        );
        vertexB.makePublic();
        assertTrue(
                vertexB.getEdgeToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeToDestinationVertex(
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
                vertexB.getEdgeToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeToDestinationVertex(
                        vertexC
                ).isPublic()
        );
        vertexB.makePrivate();
        assertFalse(
                vertexB.getEdgeToDestinationVertex(
                        vertexA
                ).isPublic()
        );
        assertFalse(
                vertexB.getEdgeToDestinationVertex(
                        vertexC
                ).isPublic()
        );
    }

    @Test
    public void make_public_changes_nb_neighbors_to_tag() {
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        vertexB.addTag(
                tag
        );
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getPrivate(),
                is(2)
        );
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getPublic(),
                is(0)
        );
        vertexB.makePublic();
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getPublic(),
                is(1)
        );
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getPrivate(),
                is(1)
        );
    }

//    @Test 
//    ("todo")
//    public void make_changes_to_edge_tags_nb_neighbors_when_changing_share_level() {
//        vertexA.makePublic();
//        EdgeOperator betweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
//        assertThat(
//                betweenAAndB.getShareLevel(),
//                is(ShareLevel.PRIVATE)
//        );
//        TagPojo tag = betweenAAndB.addTag(
//                modelTestScenarios.location()
//        ).values().iterator().next();
//        assertThat(
//                tagFactory.withUri(tag.uri()).getNbNeighbors().getPrivate(),
//                is(1)
//        );
//        assertThat(
//                tagFactory.withUri(tag.uri()).getNbNeighbors().getPublic(),
//                is(0)
//        );
//        vertexB.makePublic();
//        assertThat(
//                betweenAAndB.getShareLevel(),
//                is(ShareLevel.PUBLIC)
//        );
//        assertThat(
//                tagFactory.withUri(tag.uri()).getNbNeighbors().getPrivate(),
//                is(0)
//        );
//        assertThat(
//                tagFactory.withUri(tag.uri()).getNbNeighbors().getPublic(),
//                is(1)
//        );
//    }

    @Test
    public void can_add_vertex_and_relation_using_edge_and_vertex_uris() {
        UUID vertexId = UUID.randomUUID();
        UUID edgeId = UUID.randomUUID();
        RelationPojo newEdge = vertexA.addVertexAndRelationWithIds(
                vertexId.toString(),
                edgeId.toString()
        );
        UserUris userUris = new UserUris(vertexA.getOwnerUsername());
        URI vertexUri = userUris.vertexUriFromShortId(vertexId.toString());
        assertThat(
                newEdge.destinationUri(),
                is(vertexUri)
        );
        URI edgeUri = userUris.edgeUriFromShortId(edgeId.toString());
        assertThat(
                newEdge.uri(),
                is(edgeUri)
        );
    }

    @Test
    public void add_vertex_and_relation_sets_creation_and_last_modification_date() {
        RelationOperator newEdge = relationFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        VertexOperator newVertex = vertexFactory.withUri(
                newEdge.destinationUri()
        );
        assertThat(
                newVertex.creationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void checks_that_uris_dont_already_exist() {
        UUID vertexId = UUID.fromString(
                UserUris.graphElementShortId(vertexB.uri())
        );
        UUID edgeId = UUID.fromString(
                UserUris.graphElementShortId(vertexA.getEdgeToDestinationVertex(vertexB).uri())
        );
        RelationPojo newEdge = vertexA.addVertexAndRelationWithIds(
                vertexId.toString(),
                edgeId.toString()
        );
        UserUris userUris = new UserUris(vertexA.getOwnerUsername());
        URI vertexUri = userUris.vertexUriFromShortId(vertexId.toString());
        assertThat(
                newEdge.destinationUri(),
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
                vertexA.addVertexAndRelation().destinationUri()
        );
        assertThat(
                newVertex.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexA.makePublic();
        VertexOperator anotherNewVertex = vertexFactory.withUri(
                vertexA.addVertexAndRelation().destinationUri()
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
        Relation relation1 = farCenterVertex.addVertexAndRelation();
        Relation relation2 = farCenterVertex.addVertexAndRelation();
        assertFalse(
                vertexC.hasEdge(relation1)
        );
        assertFalse(
                vertexC.hasEdge(relation2)
        );
        farCenterVertex.mergeTo(vertexC);
        assertTrue(
                vertexC.hasEdge(relation1)
        );
        assertTrue(
                vertexC.hasEdge(relation2)
        );
    }

    @Test
    public void mergeTo_preserves_edges_direction() {
        VertexOperator farCenterVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        Relation relationOfOfFarVertex = farCenterVertex.addVertexAndRelation();
        Vertex destinationOfFarVertex = vertexFactory.withUri(relationOfOfFarVertex.destinationUri());
        relationFactory.withUri(relationOfOfFarVertex.uri()).inverse();
        assertThat(
                farCenterVertex.connectedEdges().size(),
                is(1)
        );
        assertThat(
                vertexC.connectedEdges().size(),
                is(2)
        );
        farCenterVertex.mergeTo(vertexC);
        assertThat(
                vertexC.connectedEdges().size(),
                is(3)
        );
        RelationOperator edgeOfFarVertexOperator = vertexC.connectedEdges().get(
                relationOfOfFarVertex.uri()
        );
        assertThat(
                edgeOfFarVertexOperator.sourceUri(),
                is(destinationOfFarVertex.uri())
        );
        assertThat(
                edgeOfFarVertexOperator.destinationUri(),
                is(vertexC.uri())
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
    public void mergeTo_prevents_duplication_of_relation_to_relative_when_two_vertices_share_a_common_relative() {
        assertThat(
                vertexC.getNbNeighbors().getTotal(),
                is(2)
        );
        vertexA.mergeTo(vertexC);
        wholeGraphAdmin.refreshNbNeighbors();
        assertThat(
                vertexC.getNbNeighbors().getTotal(),
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
                farVertex.addVertexAndRelation().destinationUri()
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
                farVertex.addVertexAndRelation().destinationUri()
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
                vertexB.getEdgeToDestinationVertex(vertexC).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexB.setShareLevel(
                ShareLevel.FRIENDS
        );
        assertThat(
                vertexB.getEdgeToDestinationVertex(vertexC).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexC.setShareLevel(
                ShareLevel.FRIENDS
        );
        assertThat(
                vertexB.getEdgeToDestinationVertex(vertexC).getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
        vertexB.setShareLevel(ShareLevel.PUBLIC);

        assertThat(
                vertexB.getEdgeToDestinationVertex(vertexC).getShareLevel(),
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
                is(1)
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
        TagPojo tag = relationFactory.withUri(
                vertexB.getEdgeToDestinationVertex(vertexC).uri()
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
        vertexC.getEdgeToDestinationVertex(vertexB).remove();
        vertexC.addTag(modelTestScenarios.computerScientistType());
        vertexB.addTag(modelTestScenarios.computerScientistType());
        assertFalse(vertexC.isPublic());
        vertexA.makePattern();
        assertFalse(vertexC.isPublic());
    }

    @Test
    public void sets_flag_on_edges_and_vertices_under_a_pattern() {
        assertFalse(vertexA.isUnderPattern());
        assertFalse(vertexA.getEdgeToDestinationVertex(vertexB).isUnderPattern());
        assertFalse(vertexB.isUnderPattern());
        assertFalse(vertexB.getEdgeToDestinationVertex(vertexC).isUnderPattern());
        assertFalse(vertexC.isUnderPattern());
        vertexA.makePattern();
        assertFalse(vertexA.isUnderPattern());
        assertTrue(vertexA.getEdgeToDestinationVertex(vertexB).isUnderPattern());
        assertTrue(vertexB.isUnderPattern());
        assertTrue(vertexB.getEdgeToDestinationVertex(vertexC).isUnderPattern());
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
        RelationPojo edge = vertexA.addVertexAndRelationWithIds(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        assertTrue(
                relationFactory.withUri(
                        edge.uri()
                ).isUnderPattern()
        );
        assertTrue(
                vertexFactory.withUri(
                        edge.destinationUri()
                ).isUnderPattern()
        );
        assertTrue(
                vertexFactory.withUri(
                        edge.destinationUri()
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
                vertexB.addRelationToFork(
                        farVertex.uri(),
                        vertexB.getShareLevel(),
                        farVertex.getShareLevel()
                ),
                is(nullValue())
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                vertexC.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                vertexC.addRelationToFork(
                        farVertex.uri(),
                        vertexC.getShareLevel(),
                        farVertex.getShareLevel()
                ),
                is(nullValue())
        );
        assertThat(
                vertexC.getNbNeighbors().getTotal(),
                is(2)
        );
    }


    @Test
    public void cannot_add_relation_to_distant_vertex_that_is_a_pattern_or_under_a_pattern() {
        VertexOperator farVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        VertexOperator farChildVertex = vertexFactory.withUri(
                farVertex.addVertexAndRelation().destinationUri()
        );
        farVertex.makePattern();
        assertThat(
                vertexB.addRelationToFork(
                        farChildVertex.uri(),
                        vertexB.getShareLevel(),
                        farChildVertex.getShareLevel()
                ),
                is(nullValue())
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                vertexB.addRelationToFork(
                        farVertex.uri(),
                        vertexB.getShareLevel(),
                        farVertex.getShareLevel()
                ),
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

}

