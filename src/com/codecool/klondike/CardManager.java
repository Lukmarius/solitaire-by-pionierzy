package com.codecool.klondike;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardManager {


    // Card images
    private static final String DEFAULT_URL = "card_images0";
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
                String imageFileName = baseUrl + "/" + cardName + ".png";
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

    public static boolean checkIfLowerRankOpposingColor(Card card, Pile pile) {
        int topRank = pile.getTopCard().getRank().getValue();
        int cardRank = card.getRank().getValue();
        Suit.Color topColor = pile.getTopCard().getSuit().getColor();
        Suit.Color cardColor = card.getSuit().getColor();

        return (topRank == cardRank + 1 && !topColor.equals(cardColor));
    }

    public static boolean checkIfHigherRankSameSuit(Card card, Pile pile) {
        int topRank = pile.getTopCard().getRank().getValue();
        int cardRank = card.getRank().getValue();
        Suit topSuit = pile.getTopCard().getSuit();
        Suit cardSuit = card.getSuit();

        return (topRank == cardRank - 1 && topSuit.equals(cardSuit));
    }
}
