/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.ModelTestScenarios;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;

import javax.inject.Inject;

public class RelationWithMultipleTagsScenario implements JsTestScenario {

    /*
    * Team - computer scientist -> Aria Sauley
    * the relation "computer scientist" has two identifiers with different labels
    *
    * Team to build a complex application - member -> John Foo
    *
    * the relation member has two identifiers with the same label
    */


    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator team,
            john,
            aria;

    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        buildBubbles();
        buildRelations();
        return SubGraphJson.toJson(
                userGraph.aroundForkUriInShareLevels(
                        team.uri(),
                        ShareLevel.allShareLevelsInt
                )
        );
    }

    private void buildBubbles(){
        team = vertexFactory.createForOwner(
                user.username()
        );
        team.label("Team");

        john = vertexFactory.createForOwner(
                user.username()
        );
        john.label("John Foo");

        aria = vertexFactory.createForOwner(
                user.username()
        );
        aria.label("Aria Sauley");
    }
    private void buildRelations(){
        RelationOperator scientistRelation = team.addRelationToFork(aria.uri(), team.getShareLevel(), aria.getShareLevel());
        scientistRelation.label("computer scientist");
        scientistRelation.addTag(
                modelTestScenarios.computerScientistType()
        );
        scientistRelation.addTag(
                modelTestScenarios.person()
        );

        RelationOperator memberRelation = team.addRelationToFork(john.uri(), team.getShareLevel(), john.getShareLevel());
        memberRelation.label("member");
        memberRelation.addTag(
                modelTestScenarios.person()
        );
        memberRelation.addTag(
                modelTestScenarios.personFromFreebase()
        );
    }

}
