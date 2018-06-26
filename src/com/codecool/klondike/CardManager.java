package com.codecool.klondike;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardManager {



    // Card images
    private static final String DEFAULT_URL = "card_images";
    private static Image cardBackImage;
    private static Map<String, Image> cardFaceImages = new HashMap<>();


    public static List<Card> createNewDeck() {
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit, rank, true));
            }
        }
        return result;
    }


    public static void loadCardImages() {
        loadCardImages(DEFAULT_URL);
    }

    public static void loadCardImages(String baseUrl) {
        cardBackImage = new Image(baseUrl + "/card_back.png");
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                String cardName = suit.getName() + rank;
                String cardId = "S" + suit.getValue() + "R" + rank;
                String imageFileName = "card_images/" + cardName + ".png";
                cardFaceImages.put(cardId, new Image(imageFileName));
            }
        }
    }

    public static Image getCardBackImage() {
        return cardBackImage;
    }

    public static Image getCardFaceImage(String cardId) {
        return cardFaceImages.get(cardId);
    }
}
