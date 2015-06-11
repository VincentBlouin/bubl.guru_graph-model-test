/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.module.model.graph;

import guru.bubl.module.model.User;
import guru.bubl.module.model.suggestion.Suggestion;
import guru.bubl.module.model.suggestion.SuggestionPojo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ModelTestScenarios {

    public IdentificationPojo personType() {
        return new IdentificationPojo(
                URI.create(
                        "http://xmlns.com/foaf/0.1/Person"
                ),
                new FriendlyResourcePojo(
                        "Person"
                )
        );
    }

    public IdentificationPojo computerScientistType() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/computer.computer_scientist"
                ),
                new FriendlyResourcePojo(
                        "Computer Scientist"
                )
        );
    }

    public IdentificationPojo timBernersLee() {
        return new IdentificationPojo(
                URI.create(
                        "http://www.w3.org/People/Berners-Lee/card#i"
                ),
                new FriendlyResourcePojo(
                        "Tim Berners-Lee"
                )
        );
    }

    public IdentificationPojo creatorPredicate() {
        return new IdentificationPojo(
                URI.create(
                        "http://purl.org/dc/terms/creator"
                ),
                new FriendlyResourcePojo(
                        "Creator"
                ));
    }

    public IdentificationPojo possessionIdentification() {
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                "Possession"
        );
        friendlyResourcePojo.setComment(
                "In law, possession is the control a person intentionally exercises toward a thing. In all cases, to possess something, a person must have an intention to possess it. A person may be in possession of some property. Like ownership, the possession of things is commonly regulated by states under property law."
        );
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/m/0613q"
                ),
                friendlyResourcePojo
        );
    }

    public IdentificationPojo timBernersLeeInFreebase() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/en/tim_berners-lee"
                ),
                new FriendlyResourcePojo(
                        "Tim Berners-Lee"
                )
        );
    }

    public IdentificationPojo extraterrestrial() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.example.org/extraterrestrial"
                ),
                new FriendlyResourcePojo(
                        "Extraterrestrial"
                )
        );
    }

    public IdentificationPojo location() {
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                "Location"
        );
        friendlyResourcePojo.setComment(
                "The Location type is used for any topic with a fixed location on the planet Earth. It includes geographic features such as oceans and mountains, political entities like cities and man-made objects like buildings.Guidelines for filling in location properties:geolocation: the longitude and latitude (in decimal notation) of the feature, or of the geographical center (centroid) fo the feature.contains and contained by: these properties can be used to show spatial relationships between different locations, such as an island contained by a body of water (which is equivalent to saying the body of water contains the island), a state contained by a country, a mountain within the borders of a national park, etc. For geopolitical locations,   containment two levels up and down is the ideal minimum. For example, the next two levels up for the city of Detroit are Wayne County and the state of Michigan.adjoins: also used to show spatial relations, in this case between locations that share a border.USBG Name: A unique name given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. More information can be found on their website. GNIS ID: A unique id given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. GNIS stands for Geographic Names Information System. More information can be found on their website.GEOnet Feature ID: The UFI (Unique Feature ID) used by GeoNet for features outside of the United States. More information can be found on their website."
        );
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/m/01n7"
                ),
                friendlyResourcePojo
        );
    }

    public IdentificationPojo person() {
        return new IdentificationPojo(
                URI.create(
                        "http://xmlns.com/foaf/0.1/Person"
                ),
                new FriendlyResourcePojo(
                        "Person"
                )
        );
    }

    public IdentificationPojo event() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/time/event"
                ),
                new FriendlyResourcePojo(
                        "Event"
                )
        );
    }

    public IdentificationPojo tShirt() {
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                "T-shirt"
        );
        friendlyResourcePojo.setComment(
                "A T-shirt is a style of fabric shirt, named after the T shape of the body and sleeves."
        );
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/m/013s93"
                ),
                friendlyResourcePojo
        );
    }

    public SuggestionPojo nameSuggestionFromPersonIdentification(User user) {
        return SuggestionPojo.forSameAsTypeAndOrigin(
                new FriendlyResourcePojo(
                        URI.create("http://xmlns.com/foaf/0.1/name"),
                        "Name"
                ),
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/type/text"),
                        "Text"
                ),
                Suggestion.SUGGESTION_IDENTIFICATION_PREFIX + "http://xmlns.com/foaf/0.1/Person",
                user
        );
    }

    public SuggestionPojo nameSuggestionFromSymbolIdentification(User user) {
        String symbolUri = "http://rdf.freebase.com/rdf/m/09ddf";
        return SuggestionPojo.forSameAsTypeAndOrigin(
                new FriendlyResourcePojo(
                        URI.create("http://xmlns.com/foaf/0.1/name"),
                        "Name"
                ),
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/type/text"),
                        "Text"
                ),
                Suggestion.SUGGESTION_IDENTIFICATION_PREFIX + symbolUri,
                user
        );
    }

    public SuggestionPojo startDateSuggestionFromEventIdentification(User user) {
        return SuggestionPojo.forSameAsTypeAndOrigin(
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/time/event/start_date"),
                        "Start date"
                ),
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/type/datetime"),
                        "Date"
                ),
                Suggestion.SUGGESTION_IDENTIFICATION_PREFIX + "http://rdf.freebase.com/rdf/time/event",
                user
        );
    }

    public SuggestionPojo peopleInvolvedSuggestionFromEventIdentification(User user) {
        FriendlyResourcePojo sameAs = new FriendlyResourcePojo(
                URI.create("http://rdf.freebase.com/rdf/people/person"),
                "Person"
        );
        sameAs.setComment(
                "A person is a human being (man, woman or child) known to have actually existed. Living persons, celebrities and politicians are persons, as are deceased persons.\n\nNote: A person topic is distinct from a user in Metaweb. Users have profiles that can only be edited by the users themselves. A person topic can be edited by anyone and is intended as a factual representation of details about a person.\n\nFor more information, please see the Freebase wiki page on person."
        );
        return SuggestionPojo.forSameAsTypeAndOrigin(
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/time/event/people_involved"),
                        "People involved"
                ),
                sameAs,
                Suggestion.SUGGESTION_IDENTIFICATION_PREFIX +
                        "http://rdf.freebase.com/rdf/time/event",
                user
        );
    }

    public Map<URI, SuggestionPojo> suggestionsToMap(SuggestionPojo... suggestions) {
        Map<URI, SuggestionPojo> suggestionPojoMap = new HashMap<>();
        for (SuggestionPojo suggestionPojo : suggestions) {
            suggestionPojoMap.put(
                    suggestionPojo.uri(),
                    suggestionPojo
            );
        }
        return suggestionPojoMap;
    }
}