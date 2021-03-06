/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.common.collect.Sets;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import java.net.URI;


public class GroupRelationWithImageScenario implements JsTestScenario {

    /*
     * some project-idea for 1->idea 1
     * some project-idea for 2->idea 2
     * relation "idea for" is identified to idea which has an image
     * some project-has component 1->component 1
     * some project-has component 2->component 2
     * has component is identified to component which has no images
     * some project-other relation->-other bubble
     * some project-other relation->-other bubble 2
     * some project-other relation->-other bubble 3
     */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    private VertexOperator
            someProject,
            idea1,
            idea2,
            component1,
            component2,
            otherBubble,
            otherBubble2,
            otherBubble3;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                someProject.uri(),
                ShareLevel.allShareLevelsInt
        );
        return SubGraphJson.toJson(
                subGraph
        );
    }

    private void createVertices() {
        someProject = vertexFactory.createForOwner(
                user.username()
        );
        someProject.label("some project");
        idea1 = vertexFactory.createForOwner(
                user.username()
        );
        idea1.label("idea 1");
        idea2 = vertexFactory.createForOwner(
                user.username()
        );
        idea2.label("idea 2");
        component1 = vertexFactory.createForOwner(
                user.username()
        );
        component1.label("component 1");
        component2 = vertexFactory.createForOwner(
                user.username()
        );
        component2.label("component 2");
        otherBubble = vertexFactory.createForOwner(
                user.username()
        );
        otherBubble.label("other bubble");
        otherBubble2 = vertexFactory.createForOwner(
                user.username()
        );
        otherBubble2.label("other bubble 2");
        otherBubble3 = vertexFactory.createForOwner(
                user.username()
        );
        otherBubble3.label("other bubble 3");
    }

    private void createEdges() {
        FriendlyResourcePojo ideaFriendlyResource = new FriendlyResourcePojo(
                "idea"
        );
        ideaFriendlyResource.setImages(Sets.newHashSet(
                Image.withUrlForSmallAndUriForBigger(
                        "dummy_base_64",
                        URI.create("big_url")
                )
        ));
        TagPojo ideaIdentification = new TagPojo(
                URI.create(
                        "http://external-uri.com/idea"
                ),
                ideaFriendlyResource
        );
        RelationOperator rIdea1 = someProject.addRelationToFork(idea1.uri(), someProject.getShareLevel(), idea1.getShareLevel());
        rIdea1.label("idea for 1");
        rIdea1.addTag(ideaIdentification);

        RelationOperator rIdea2 = someProject.addRelationToFork(idea2.uri(), someProject.getShareLevel(), idea2.getShareLevel());
        rIdea2.label("idea for 2");
        rIdea2.addTag(ideaIdentification);

        TagPojo componentIdentification = new TagPojo(
                URI.create(
                        "http://external-uri.com/component"
                ),
                new FriendlyResourcePojo(
                        "component"
                )
        );
        RelationOperator rComponent1 = someProject.addRelationToFork(component1.uri(), someProject.getShareLevel(), component1.getShareLevel());
        rComponent1.label("has component 1");
        rComponent1.addTag(componentIdentification);

        RelationOperator rComponent2 = someProject.addRelationToFork(component2.uri(), someProject.getShareLevel(), component2.getShareLevel());
        rComponent2.label("has component 2");
        rComponent2.addTag(componentIdentification);

        someProject.addRelationToFork(
                otherBubble.uri(),
                someProject.getShareLevel(),
                otherBubble.getShareLevel()
        ).label("other relation");

        someProject.addRelationToFork(
                otherBubble2.uri(),
                someProject.getShareLevel(),
                otherBubble2.getShareLevel()
        ).label("other relation");

        someProject.addRelationToFork(
                otherBubble3.uri(),
                someProject.getShareLevel(),
                otherBubble3.getShareLevel()
        ).label("other relation");
    }
}
