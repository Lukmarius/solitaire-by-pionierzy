package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.*;

public class Game extends Pane {

    // Number of piles
    private static final int FOUNDATIONS = Suit.values().length;
    private static final int TABLEAUS = 7;
    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;
    private static int currentBackgroundThemeNumber = 0;
    private static int allBgThemes = 5; // = numOfFiles() method if worked
    private static int currentCardThemeNumber = 0;
    private static int allCardThemes = 2;
    private List<Card> deck = new ArrayList<>();
    // Piles
    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();
    private List<Pile> placeablePiles = FXCollections.observableArrayList();
    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();
    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();
        if (e.getClickCount() == 2 && !e.isConsumed() && card.equals(card.getContainingPile().getTopCard())) {
            e.consume();

            for (Pile pile : foundationPiles) {
                Card topCard = pile.getTopCard();
                if (topCard == null) {
                    if (card.getRank().equals(Rank.Ace)) card.moveToPile(pile);
                    break;
                } else {
                    int topRank = topCard.getRank().getValue();
                    int cardRank = card.getRank().getValue();
                    if (topCard.getSuit().equals(card.getSuit()) && topRank == cardRank - 1) {
                        card.moveToPile(pile);
                        break;
                    }
                }
            }
        } else {
            if (card.getContainingPile().getPileType() == Pile.PileType.STOCK) {
                card.moveToPile(discardPile);
                card.flip();
                card.setMouseTransparent(false);
                System.out.println("Placed " + card + " to the waste.");
            }
        }
    };
    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        refillStockFromDiscard();
    };
    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
    };


    // This method should count number of files in the directory, but path contains user name
//    private int numOfFiles(){
//        File dir = new File("/home/mariusz/IdeaProjects/oop-solitaire/resources/table");
//        int numberOfSubfolders = 0;
//        File listDir[] = dir.listFiles();
//        return listDir.length;
//    }
    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {
        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        if (activePile.getPileType() == Pile.PileType.STOCK)
            return;
        if (card.isFaceDown()) {
            return;
        }
        double offsetX = e.getSceneX() - dragStartX;
        double offsetY = e.getSceneY() - dragStartY;

        draggedCards.clear();
        ListIterator<Card> it = activePile.getCards().listIterator();
        Card currentCard = it.hasNext() ? it.next() : null;
        while (currentCard != null && !card.equals(currentCard)) {
            currentCard = it.next();
        }
        draggedCards.add(card);
        while (it.hasNext()) {
            currentCard = it.next();
            draggedCards.add(currentCard);
        }

        card.getDropShadow().setRadius(20);
        card.getDropShadow().setOffsetX(10);
        card.getDropShadow().setOffsetY(10);

        card.toFront();
        card.setTranslateX(offsetX);
        card.setTranslateY(offsetY);
    };
    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;

        Card card = (Card) e.getSource();
        Pile pile = getValidIntersectingPile(card, placeablePiles);
        //TODO
        if (pile != null) {
            handleValidMove(card, pile);
        } else {
            draggedCards.forEach(MouseUtil::slideBack);
            draggedCards = FXCollections.observableArrayList();
        }
    };

    public Game() {
        setThemeButton();
        deck = CardManager.createNewDeck();
        shuffleDeck();
        initPiles();
        dealCards();
        flipTopCards();
    }

    public void setThemeButton() {
        // Background theme:
        Button switchBgThemeButton = new Button("Background");
        switchBgThemeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchBackgroundTheme();
            }
        });
        getChildren().add(switchBgThemeButton);

        // Card Theme:
        Button switchCardThemeButton = new Button("Cards");
        switchCardThemeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchCardTheme();
            }
        });
        getChildren().add(switchCardThemeButton);
        switchCardThemeButton.setLayoutY(260);
    }

    private void switchBackgroundTheme() {
        if (currentBackgroundThemeNumber < allBgThemes - 1) {
            currentBackgroundThemeNumber++;
        } else {
            currentBackgroundThemeNumber = 0;
        }
        setTableBackground(new Image("/table/bg" + currentBackgroundThemeNumber));
    }

    private void switchCardTheme() {
        if (currentCardThemeNumber < allCardThemes - 1) {
            currentCardThemeNumber++;
        } else {
            currentCardThemeNumber = 0;
        }
        CardManager.loadCardImages("card_images" + currentCardThemeNumber);
        for (Card card : this.deck) {
            card.setFrontFace();
        }
    }

    public boolean isGameWon() {
        //TODO
        return false;
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }

    public void refillStockFromDiscard() {
        //TODO
        System.out.println("Stock refilled from discard pile.");
    }

    public boolean isMoveValid(Card card, Pile destPile) {
        //TODO
        if (destPile.getPileType() == Pile.PileType.TABLEAU) {
            if (destPile.isEmpty() && card.getRank().equals(Rank.King)) return true;
            else if (!destPile.isEmpty()) return CardManager.checkIfLowerRankOpposingColor(card, destPile);
        } else if (destPile.getPileType() == Pile.PileType.FOUNDATION) {
            if (destPile.isEmpty() && card.getRank() == Rank.Ace) return true;
            else if (!destPile.isEmpty()) return CardManager.checkIfHigherRankSameSuit(card, destPile);
        }
        return false;
    }

    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty())
            return card.getBoundsInParent().intersects(pile.getBoundsInParent());
        else
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(Pile.PileType.TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile);
        draggedCards.clear();
    }


    private void initPiles() {
        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(95);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(285);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);

        for (int i = 0; i < FOUNDATIONS; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(610 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
            placeablePiles.add(foundationPile);
        }
        for (int i = 0; i < TABLEAUS; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(95 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
            placeablePiles.add(tableauPile);
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.deck);
    }

    public void dealCards() {
        Iterator<Card> deckIterator = deck.iterator();

        for (int i = 0; i < TABLEAUS; i++) {
            int nCards = i + 1;
            for (int j = 0; j < nCards; j++) {
                Card card = deckIterator.next();
                tableauPiles.get(i).addCard(card);
                addMouseEventHandlers(card);
                getChildren().add(card);
            }
        }

        deckIterator.forEachRemaining(card -> {
            stockPile.addCard(card);
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    public void flipTopCards() {
        for (Pile pile : this.tableauPiles) {
            pile.getTopCard().flip();
        }
    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

}
