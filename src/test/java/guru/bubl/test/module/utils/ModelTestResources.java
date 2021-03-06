/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.utils;

import guru.bubl.module.model.FriendlyResourceFactory;
import guru.bubl.module.model.ModelTestScenarios;
import guru.bubl.module.model.User;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.graph_element.GraphElement;
import guru.bubl.module.model.graph.graph_element.GraphElementOperatorFactory;
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.pattern.PatternUserFactory;
import guru.bubl.module.model.graph.relation.RelationFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.tag.TagFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.search.GraphSearchFactory;
import guru.bubl.module.model.test.SubGraphOperator;
import guru.bubl.module.model.test.scenarios.GraphElementsOfTestScenario;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.neo4j_graph_manipulator.graph.embedded.admin.WholeGraphNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.UserGraphFactoryNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.extractor.subgraph.SubGraphExtractorFactoryNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.vertex.VertexFactoryNeo4j;
import guru.bubl.module.repository.user.UserRepository;
import guru.bubl.test.module.model.user.FriendManagerTest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import javax.inject.Inject;

import java.net.URI;
import java.util.Date;

import static org.neo4j.driver.Values.parameters;

public class ModelTestResources {

    @Inject
    protected GroupRelationFactory groupRelationFactory;

    @Inject
    protected CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    @Inject
    protected CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @Inject
    protected FriendlyResourceFactory friendlyResourceFactory;

    @Inject
    protected GraphElementOperatorFactory graphElementOperatorFactory;

    @Inject
    protected Driver driver;

    @Inject
    public ModelTestScenarios modelTestScenarios;

    @Inject
    protected TestScenarios testScenarios;

    @Inject
    protected SubGraphExtractorFactoryNeo4j neo4jSubGraphExtractorFactory;

    @Inject
    public WholeGraphNeo4j wholeGraph;

    @Inject
    protected RelationFactory relationFactory;

    @Inject
    protected VertexFactoryNeo4j vertexFactory;

    @Inject
    protected UserGraphFactoryNeo4j userGraphFactory;

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected GraphSearchFactory graphSearchFactory;

    @Inject
    protected TagFactory tagFactory;

    @Inject
    protected PatternUserFactory patternUserFactory;

    @Inject
    protected WholeGraphAdmin wholeGraphAdmin;

    @Inject
    protected UserRepository userRepository;

    @Inject
    protected FriendManagerFactory friendManagerFactory;

    protected VertexOperator vertexA;
    protected VertexOperator vertexB;
    protected VertexOperator vertexC;
    protected VertexOperator vertexD;
    protected VertexOperator vertexE;
    protected GroupRelationOperator groupRelation;

    protected static User user;

    protected static User anotherUser;
    protected static UserGraph anotherUserGraph;
    protected static VertexOperator vertexOfAnotherUser;

    protected UserGraph userGraph;

    protected User thirdUser;
    protected VertexOperator thirdUserVertex;

    protected GraphElementsOfTestScenario graphElementsOfTestScenario;

    @Before
    public void before() {
        ModelTestRunner.injector.injectMembers(this);
        removeAll();
        user = User.withEmail(
                "roger.lamothe@example.org"
        ).setUsername("roger_lamothe").setPreferredLocales("[en]").password("12345678");
        user = userRepository.createUser(user);
        anotherUser = User.withEmail(
                "colette.armande@example.org"
        ).setUsername("colette_armande").setPreferredLocales("[fr]").password("12345678");
        userRepository.createUser(anotherUser);
        userGraph = userGraphFactory.withUser(user);
        graphElementsOfTestScenario = testScenarios.buildTestScenario(
                userGraph
        );
        vertexA = graphElementsOfTestScenario.getVertexA();
        vertexB = graphElementsOfTestScenario.getVertexB();
        vertexC = graphElementsOfTestScenario.getVertexC();
        groupRelation = graphElementsOfTestScenario.getGroupRelation();
        vertexD = graphElementsOfTestScenario.getVertexD();
        vertexE = graphElementsOfTestScenario.getVertexE();
        anotherUserGraph = userGraphFactory.withUser(anotherUser);
        vertexOfAnotherUser = vertexFactory.withUri(
                anotherUserGraph.createVertex().uri()
        );
        vertexOfAnotherUser.label("vertex of another user");
    }

    protected SubGraphPojo wholeGraphAroundDefaultCenterVertex() {
        Integer depthThatShouldCoverWholeGraph = 1000;
        return neo4jSubGraphExtractorFactory.withCenterVertexInShareLevelsAndDepth(
                vertexA.uri(),
                depthThatShouldCoverWholeGraph,
                ShareLevel.allShareLevelsInt
        ).load();
    }

    protected int numberOfEdgesAndVertices() {
        return numberOfVertices() +
                numberOfEdges();
    }

    public User user() {
        return user;
    }

    protected SubGraphOperator wholeGraph() {
        return SubGraphOperator.withVerticesAndEdges(
                wholeGraph.getAllVertices(),
                wholeGraph.getAllEdges()
        );
    }

    protected int numberOfVertices() {
        return wholeGraph.getAllVertices().size();
    }

    protected int numberOfEdges() {
        return wholeGraph.getAllEdges().size();
    }

    protected VertexPojo getVertexWithLabel(SubGraphPojo subGraph, String label) {
        VertexPojo vertexWithLabel = null;
        for (VertexPojo vertex : subGraph.vertices().values()) {
            if (vertex.label().equals(label)) {
                vertexWithLabel = vertex;
            }
        }
        return vertexWithLabel;
    }

    protected void removeAll() {
        try (Session session = driver.session()) {
            session.run(
                    "MATCH (n:Resource) DETACH DELETE n"
            );
            session.run(
                    "MATCH (n:Notification) DETACH DELETE n"
            );
        }
    }

    protected void setupThirdUser() {
        thirdUser = User.withEmail(
                "tres.usuario@example.org"
        ).setUsername("tres").setPreferredLocales("[es]").password("12345678");
        thirdUser = userRepository.createUser(thirdUser);
        thirdUserVertex = vertexFactory.withUri(
                userGraphFactory.withUser(thirdUser).createVertex().uri()
        );
        thirdUserVertex.label("vértice");
    }

    protected void makeAllPublic() {
        vertexA.makePublic();
        vertexB.makePublic();
        vertexC.makePublic();
        vertexD.makePublic();
        vertexE.makePublic();
        groupRelation.makePublic();
    }

    protected void setLastModificationDate(URI uri, Date date) {
        try (Session session = driver.session()) {
            session.run(
                    "MATCH (n:Resource{uri:$uri}) set n.last_modification_date=$lastModificationDate",
                    parameters(
                            "uri", uri.toString(),
                            "lastModificationDate", date.getTime()
                    )
            );
        }
    }
}