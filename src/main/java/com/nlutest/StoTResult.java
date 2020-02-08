
package com.nlutest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


//fields returned by transcribing
class Alternative {

    String transcript;
    Float confidence;

}

@JsonDeserialize(as = StoTResult.class)
class StoTResult {

    //to deserialize a java key word!
    @JsonProperty(value = "final")
    Boolean xfinal;

    Alternative[] alternatives;

}

//{
//  "results": [
//    {
//      "final": true,
//      "alternatives": [
//        {
//          "transcript": "several tornadoes touch down as a line of severe thunderstorms swept through Colorado on Sunday ",
//          "confidence": 0.96
//        }
//      ]
//    }
//  ],
//  "result_index": 0
//}

//{
//  "results": [],
//  "result_index": 0
//}




//{
//  "results": [
//    {
//      "final": true,
//      "alternatives": [
//        {
//          "transcript": "your ",
//          "confidence": 0.23
//        }
//      ]
//    },
//    {
//      "final": true,
//      "alternatives": [
//        {
//          "transcript": "%HESITATION ",
//          "confidence": 0.32
//        }
//      ]
//    },
//    {
//      "final": true,
//      "alternatives": [
//        {
//          "transcript": "but I don't know hi my friend lives there has been this one came just staying in line the room was ",
//          "confidence": 0.55
//        }
//      ]
//    },
//    {
//      "final": true,
//      "alternatives": [
//        {
//          "transcript": "hello ",
//          "confidence": 0.34
//        }
//      ]
//    }
//  ],
//  "result_index": 0
//}
//[{
//  "final": true,
//  "alternatives": [
//    {
//      "transcript": "your ",
//      "confidence": 0.23
//    }
//  ]
//}, {
//  "final": true,
//  "alternatives": [
//    {
//      "transcript": "%HESITATION ",
//      "confidence": 0.32
//    }
//  ]
//}, {
//  "final": true,
//  "alternatives": [
//    {
//      "transcript": "but I don't know hi my friend lives there has been this one came just staying in line the room was ",
//      "confidence": 0.55
//    }
//  ]
//}, {
//  "final": true,
//  "alternatives": [
//    {
//      "transcript": "hello ",
//      "confidence": 0.34
//    }
//  ]
//}]
