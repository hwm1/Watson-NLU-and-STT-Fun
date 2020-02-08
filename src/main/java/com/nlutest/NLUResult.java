
package com.nlutest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

class Entities {

    String type;
    String text;
    Float relevance;
    Float confidence;
    int count;
    Emotion emotion;
    Sentiment sentiment;
    Disambiguation disambiguation;
}

class Sentiment {

    Float score;
}

class Disambiguation {

    String name;
    String dbpedia_resource;
}

class Emotion {

    Float anger;
    Float disgust;
    Float fear;
    Float joy;
    Float sadness;
}

class Keywords {

    int count;
    Float relevance;
    String text;
    Emotion emotion;
    Sentiment sentiment;
}

class Usage {

    int features;
    int text_characters;
    int text_units;
}

 
@JsonDeserialize(as = NLUResult.class)
class NLUResult {

    String language;
    Usage usage;
    Entities entities[];
    Keywords keywords[];
}