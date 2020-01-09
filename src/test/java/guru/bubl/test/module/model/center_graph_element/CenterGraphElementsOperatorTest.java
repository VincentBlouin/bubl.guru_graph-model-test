/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.friend.FriendManager;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CenterGraphElementsOperatorTest extends ModelTestResources {

    @Inject
    FriendManagerFactory friendManagerFactory;

    @Test
    public void does_not_return_center_elements_of_another_user() {
        Integer defaultUserNbCenters = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        ).size();

        Integer anotherUserNbCenters = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                anotherUser
        ).size();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexC
        ).updateLastCenterDate();
        assertThat(
                centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                        user
                ).size(),
                is(
                        defaultUserNbCenters + 1
                )
        );
        assertThat(
                centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                        anotherUser
                ).size(),
                is(
                        anotherUserNbCenters
                )
        );
    }


    @Test
    public void can_get_only_public_bubbles() {
        centerGraphElementOperatorFactory.usingFriendlyResource(vertexA).updateLastCenterDate();
        Integer nbPublicCenters = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().size();
        vertexA.makePublic();
        assertThat(
                centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().size(),
                is(
                        nbPublicCenters + 1
                )
        );
    }

    @Test
    public void does_not_return_public_bubble_if_not_a_center_bubble() {
        assertThat(
                centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().size(),
                is(
                        0
                )
        );
        vertexB.makePublic();
        assertThat(
                centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().size(),
                is(
                        0
                )
        );
    }

    @Test
    public void includes_public_context_only_if_fetching_all_public_only_centers() {
        vertexA.makePublic();
        vertexB.makePublic();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).updateLastCenterDate();
        graphIndexer.indexVertex(
                vertexA
        );
        CenterGraphElementPojo centerGraphElement = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().iterator().next();
        assertFalse(
                centerGraphElement.getContext().isEmpty()
        );
        vertexB.makePrivate();
        graphIndexer.indexVertex(
                vertexA
        );
        centerGraphElement = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().iterator().next();
        assertTrue(
                centerGraphElement.getContext().isEmpty()
        );
    }

    @Test
    public void includes_context() {
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        graphIndexer.indexVertex(
                vertexA
        );
        CenterGraphElementPojo centerGraphElement = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        ).iterator().next();
        assertThat(
                centerGraphElement.getContext().values().iterator().next(),
                is("vertex B")
        );
    }

    @Test
    public void returns_number_of_references_of_center_metas() {
        TagPojo meta = vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                meta
        ).updateLastCenterDate();
        List<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        );
        CenterGraphElementPojo centerMeta = null;
        for (CenterGraphElementPojo centerGraphElement : centerGraphElements) {
            if (UserUris.isUriOfAnIdentifier(centerGraphElement.getGraphElement().uri())) {
                centerMeta = centerGraphElement;
            }
        }
        assertThat(
                centerMeta.getNbReferences(),
                is(1)
        );
    }

    @Test
    public void can_limit() {
        CenterGraphElementOperator centerA = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerA.updateLastCenterDate();
        centerA.incrementNumberOfVisits();

        CenterGraphElementOperator centerB = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        centerB.updateLastCenterDate();
        centerB.incrementNumberOfVisits();

        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        );
        assertThat(
                centers.size(),
                Matchers.is(2)
        );
        centers = centerGraphElementsOperatorFactory.usingLimitAndSkip(
                1, 0
        ).getPublicAndPrivateForOwner(user);
        assertThat(
                centers.size(),
                Matchers.is(1)
        );
    }

    @Test
    public void can_paginate() {
        CenterGraphElementOperator centerA = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerA.updateLastCenterDate();
        centerA.incrementNumberOfVisits();

        CenterGraphElementOperator centerB = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        centerB.updateLastCenterDate();
        centerB.incrementNumberOfVisits();

        CenterGraphElementPojo center = centerGraphElementsOperatorFactory.usingLimitAndSkip(
                1, 0
        ).getPublicAndPrivateForOwner(user).iterator().next();
        assertThat(
                center.getGraphElement().label(),
                Matchers.is("vertex B")
        );
        center = centerGraphElementsOperatorFactory.usingLimitAndSkip(
                1, 1
        ).getPublicAndPrivateForOwner(user).iterator().next();
        assertThat(
                center.getGraphElement().label(),
                Matchers.is("vertex A")
        );
    }

    @Test
    public void getting_all_public_does_not_return_private() {
        vertexA.makePublic();
        CenterGraphElementOperator centerA = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerA.updateLastCenterDate();
        centerA.incrementNumberOfVisits();

        vertexB.makePrivate();
        CenterGraphElementOperator centerB = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        centerB.updateLastCenterDate();
        centerB.incrementNumberOfVisits();

        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic();

        assertThat(
                centers.size(),
                is(1)
        );
    }

    @Test
    public void can_get_patterns() {
        vertexA.makePublic();
        vertexB.makePublic();
        CenterGraphElementOperator centerA = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerA.updateLastCenterDate();
        centerA.incrementNumberOfVisits();
        CenterGraphElementOperator centerB = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        centerB.updateLastCenterDate();
        centerB.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPatterns();
        assertThat(
                centers.size(),
                is(0)
        );
        vertexA.makePattern();
        centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPatterns();
        assertThat(
                centers.size(),
                is(1)
        );
    }

    @Test
    public void can_get_for_friends() {
        vertexOfAnotherUser.setShareLevel(ShareLevel.FRIENDS);
        CenterGraphElementOperator centerOfAnotherUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexOfAnotherUser
        );
        centerOfAnotherUser.updateLastCenterDate();
        centerOfAnotherUser.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getFriendsFeedForUser(
                user
        );
        assertThat(
                centers.size(),
                is(0)
        );
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        friendManagerFactory.forUser(anotherUser).confirm(
                user
        );
        centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getFriendsFeedForUser(
                user
        );
        assertThat(
                centers.size(),
                is(1)
        );
    }

    @Test
    public void cant_get_for_friends_if_friendship_is_not_confirmed() {
        vertexOfAnotherUser.setShareLevel(ShareLevel.FRIENDS);
        CenterGraphElementOperator centerOfAnotherUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexOfAnotherUser
        );
        centerOfAnotherUser.updateLastCenterDate();
        centerOfAnotherUser.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getFriendsFeedForUser(
                user
        );
        assertThat(
                centers.size(),
                is(0)
        );
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getFriendsFeedForUser(
                user
        );
        assertThat(
                centers.size(),
                is(0)
        );
    }

    @Test
    public void cant_get_friends_private_centers() {
        vertexOfAnotherUser.setShareLevel(ShareLevel.PRIVATE);
        CenterGraphElementOperator centerOfAnotherUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexOfAnotherUser
        );
        centerOfAnotherUser.updateLastCenterDate();
        centerOfAnotherUser.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getFriendsFeedForUser(user);
        assertThat(
                centers.size(),
                is(0)
        );
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        friendManagerFactory.forUser(anotherUser).confirm(
                user
        );
        centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getFriendsFeedForUser(
                user
        );
        assertThat(
                centers.size(),
                is(0)
        );
    }

    @Test
    public void can_get_for_a_specific_friend() {
        vertexOfAnotherUser.setShareLevel(ShareLevel.FRIENDS);
        CenterGraphElementOperator centerOfAnotherUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexOfAnotherUser
        );
        centerOfAnotherUser.updateLastCenterDate();
        centerOfAnotherUser.incrementNumberOfVisits();
        setupThirdUser();
        thirdUserVertex.setShareLevel(ShareLevel.FRIENDS);
        CenterGraphElementOperator centerOfThirdUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                thirdUserVertex
        );
        centerOfThirdUser.updateLastCenterDate();
        centerOfThirdUser.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getForAFriend(thirdUser);
        assertThat(
                centers.size(),
                is(1)
        );
        assertThat(
                centers.iterator().next().getGraphElement().label(),
                is("vértice")
        );
        centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getForAFriend(anotherUser);
        assertThat(
                centers.size(),
                is(1)
        );
        assertThat(
                centers.iterator().next().getGraphElement().label(),
                is("vertex of another user")
        );
    }

    @Test
    public void cant_get_private_for_a_specific_friend() {
        vertexOfAnotherUser.setShareLevel(ShareLevel.PRIVATE);
        CenterGraphElementOperator centerOfAnotherUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexOfAnotherUser
        );
        centerOfAnotherUser.updateLastCenterDate();
        centerOfAnotherUser.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getForAFriend(anotherUser);
        assertThat(
                centers.size(),
                is(0)
        );
    }


    @Test
    public void can_get_public_for_a_specific_user() {
        vertexOfAnotherUser.setShareLevel(ShareLevel.PUBLIC);
        CenterGraphElementOperator centerOfAnotherUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexOfAnotherUser
        );
        centerOfAnotherUser.updateLastCenterDate();
        centerOfAnotherUser.incrementNumberOfVisits();
        setupThirdUser();
        thirdUserVertex.setShareLevel(ShareLevel.PUBLIC);
        CenterGraphElementOperator centerOfThirdUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                thirdUserVertex
        );
        centerOfThirdUser.updateLastCenterDate();
        centerOfThirdUser.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicOfUser(thirdUser);
        assertThat(
                centers.size(),
                is(1)
        );
        assertThat(
                centers.iterator().next().getGraphElement().label(),
                is("vértice")
        );
        centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicOfUser(anotherUser);
        assertThat(
                centers.size(),
                is(1)
        );
        assertThat(
                centers.iterator().next().getGraphElement().label(),
                is("vertex of another user")
        );
    }

    @Test
    public void cant_get_private_for_a_specific_user() {
        vertexOfAnotherUser.setShareLevel(ShareLevel.PRIVATE);
        CenterGraphElementOperator centerOfAnotherUser = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexOfAnotherUser
        );
        centerOfAnotherUser.updateLastCenterDate();
        centerOfAnotherUser.incrementNumberOfVisits();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicOfUser(anotherUser);
        assertThat(
                centers.size(),
                is(0)
        );
    }

    @Test
    public void returns_number_of_connected_edges() {
        CenterGraphElementOperator vertexBCenter = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        vertexBCenter.updateLastCenterDate();
        vertexBCenter.incrementNumberOfVisits();
        CenterGraphElementPojo center = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        ).iterator().next();
        assertThat(
                center.getNumberOfConnectedEdges(),
                is(2)
        );
    }

    @Test
    public void does_not_return_number_of_connected_edges_for_not_owned_vertices() {
        vertexB.makePublic();
        CenterGraphElementOperator vertexBCenter = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        vertexBCenter.updateLastCenterDate();
        vertexBCenter.incrementNumberOfVisits();
        CenterGraphElementPojo center = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicOfUser(
                user
        ).iterator().next();
        assertThat(
                center.getNumberOfConnectedEdges(),
                is(nullValue())
        );
    }

    @Test
    public void returns_number_of_public() {
        vertexB.makePublic();
        vertexC.makePublic();
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        CenterGraphElementOperator vertexBCenter = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        vertexBCenter.updateLastCenterDate();
        vertexBCenter.incrementNumberOfVisits();
        CenterGraphElementPojo center = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicOfUser(
                user
        ).iterator().next();
        assertThat(
                center.getNbPublicNeighbors(),
                is(1)
        );
        assertThat(
                center.getNbFriendNeighbors(),
                is(nullValue())
        );
    }

    @Test
    public void returns_number_of_public_and_friends() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        friendManagerFactory.forUser(anotherUser).confirm(
                user
        );
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        vertexB.setShareLevel(ShareLevel.FRIENDS);
        vertexC.setShareLevel(ShareLevel.FRIENDS);
        CenterGraphElementOperator vertexBCenter = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        vertexBCenter.updateLastCenterDate();
        vertexBCenter.incrementNumberOfVisits();
        CenterGraphElementPojo center = centerGraphElementsOperatorFactory.usingDefaultLimits().getFriendsFeedForUser(
                anotherUser
        ).iterator().next();
        assertThat(
                center.getNbFriendNeighbors(),
                is(2)
        );
        assertThat(
                center.getNbPublicNeighbors(),
                is(0)
        );
        assertThat(
                center.getNumberOfConnectedEdges(),
                is(nullValue())
        );
    }
}
