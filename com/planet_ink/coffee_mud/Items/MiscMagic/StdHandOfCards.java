package com.planet_ink.coffee_mud.Items.MiscMagic;
import com.planet_ink.coffee_mud.Items.Basic.StdContainer;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;


/* 
    Copyright (c) 2005-2007, Bo Zimmerman
    All rights reserved.

    Redistribution and use in source and binary forms, with or without modification, are permitted provided that the 
    following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following 
    disclaimer. 

    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following 
    disclaimer in the documentation and/or other materials provided with the distribution. 

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
    INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
    SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
    WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

    Hand Of Cards
    This object represents an arbitarily sized collection of
    PlayingCard objects.  It includes numerous methods for
    retreiving and adding cards, shuffling and sorting, and
    most expecially for returning a message of turned-up cards
    to anyone who LOOKS at a person holding a hand with such
    cars.
*/
public class StdHandOfCards extends StdContainer implements MiscMagic, HandOfCards
{
    public String ID(){ return "StdHandOfCards";}
    
    // if this hand or deck is owned by a mob or a room, then
    // than mob or room suffices as a container.  Otherwise,
    // we need an internal container to keep track.
    private Vector backupContents=new Vector();
    
    // the constructor for this class doesn't do much except set
    // some of the display properties of the deck container
    public StdHandOfCards()
    {
        super();
        setName("a hand of cards");
        setDisplayText("a pile of cards lay here");
        setDescription("");
        
        // uncomment below for added security
        //CMLib.flags().setGettable(this,false);
        //CMLib.flags().setDroppable(this,false);
        //CMLib.flags().setRemovable(this,false);
        baseEnvStats().setWeight(1);
        // capacity is 52 cards + 1 for the deck itself.
        setCapacity(53);
        // this type is arbitrary -- we will override canContain method
        setContainTypes(Container.CONTAIN_SSCOMPONENTS);
        backupContents=new Vector();
        recoverEnvStats();
    }

    // getContents()
    // this method is an override of the container object
    // method of the same name.  If is necessary because
    // if the hand or deck is owned by a mob or a room, then
    // than mob or room suffices as a container.  Otherwise,
    // we need to use the internal container to keep track.
    public Vector getContents()
    {
        if((owner instanceof MOB)||(owner instanceof Room))
            return super.getContents();
        return (Vector)backupContents.clone();
    }
    
    // shuffleDeck()
    // Shuffles the deck by removing a random card from the
    // middle of the deck and adding it to the bottom
    // 52*5 times.
    public boolean shuffleDeck()
    {
        Vector V=getContents();
        Environmental own=owner();
        if(V.size()==0)
            return false;
        for(int i=0;i<V.size()*5;i++)
        {
            Item I=(Item)V.elementAt(CMLib.dice().roll(1,V.size(),-1));
            I.setContainer(this);
            if(own instanceof MOB)
            {
                I.removeFromOwnerContainer();
                ((MOB)own).addInventory(I);
            }
            else
            if(own instanceof Room)
            {
                I.removeFromOwnerContainer();
                ((Room)own).addItemRefuse(I,Item.REFUSE_PLAYER_DROP);
            }
            else
            {
                backupContents.removeElement(I);
                backupContents.addElement(I);
            }
        }
        return true;
    }
    
    // getTopCardFromDeck()
    // returns the top card item object from the deck 
    public PlayingCard getTopCardFromDeck()
    {
        Vector deckContents=getContents();
        if(deckContents.size()==0) return null;
        PlayingCard card=(PlayingCard)deckContents.firstElement();
        if(card.container() instanceof HandOfCards)
            ((HandOfCards)card.container()).removeCard(card);
        return card;
    }
    
    // addCard(PlayingCard card)
    // returns the given card item object to 
    // the deck by removing it from its current
    // owner and adding it back to the decks owner
    // and container.  If doing this causes a players 
    // hand to be devoid of cards, the hand container
    // is destroyed.
    public boolean addCard(PlayingCard card)
    {
        if(card==null) return false;
        if(card.container() instanceof HandOfCards)
            ((HandOfCards)card.container()).removeCard(card);
        
        if(owner() instanceof MOB)
        {
            if(card.owner()==null)
                ((MOB)owner()).addInventory(card);
            else
            if(!((MOB)owner()).isMine(card))
                ((MOB)owner()).giveItem(card);
        }
        else
        if(owner() instanceof Room)
        {
            if(card.owner()==null)
                ((Room)owner()).addItem(card);
            else
            if(!((Room)owner()).isContent(card))
                ((Room)owner()).bringItemHere(card,Item.REFUSE_PLAYER_DROP,false);
        }
        else
        {
            card.setOwner(card);
            if(!backupContents.contains(card))
                backupContents.addElement(card);
        }
        card.setContainer(this);
        if(owner() instanceof MOB)
            return ((MOB)owner()).isMine(card);
        else
        if(owner() instanceof Room)
            return ((Room)owner()).isContent(card);
        return true;
    }

    // numberOfCards()
    // returns the current number of cards
    // in the deck.
    public int numberOfCards()
    {
        Vector deckContents=getContents();
        return deckContents.size();
    }

    // removeCard(PlayingCard card)
    // removes the given card from the 
    // deck and places it in limbo.  calls
    // to this method should be followed
    // by an addCard method on another deck.
    public boolean removeCard(PlayingCard card)
    {
        Vector handContents=getContents();
        if(handContents.contains(card))
        {
            if((card.owner() instanceof MOB)
            ||(card.owner() instanceof Room))
                card.removeFromOwnerContainer();
            else
                backupContents.remove(card);
            return true;
        }
        return false;
    }

    // removeAllCards()
    // removes all cards from the deck and
    // places them in limbo.  Calls to this
    // method should be followed by either
    // a destroy method on the cards themselves
    // or an addCard method on another deck.
    public boolean removeAllCards()
    {
        Vector handContents=getContents();
        if(handContents.size()==0) return false;
        for(int i=0;i<handContents.size();i++)
            removeCard((PlayingCard)handContents.elementAt(i));
        return true;
    }
    
    
    // getContentsEncoded()
    // This method builds a string array equal in size to the deck.
    // It then returns the contents of the deck encoded in 
    // cardStringCode format.  See convertCardBitCodeToCardStringCode
    public String[] getContentsEncoded()
    {
        Vector contents=getContents();
        String[] encodedDeck=new String[contents.size()];
        for(int i=0;i<contents.size();i++)
        {
            PlayingCard card=(PlayingCard)contents.elementAt(i);
            encodedDeck[i]=card.getStringEncodedSuit()+card.getStringEncodedValue();
        }
        return encodedDeck;
    }
    
    // sortByValueAceHigh()
    // This method is a sort of anti-shuffle.  It puts the cards in
    // order, first by value, then by suit, with ace considered high.
    public void sortByValueAceHigh()
    {
        Vector unsorted=getContents();
        if(unsorted.size()==0) return;
        // first step is to get them out of the deck and
        // then re-add them in order.
        for(int u=0;u<unsorted.size();u++)
            removeCard((PlayingCard)unsorted.elementAt(u));
        
        // now we pick them out in order
        while(unsorted.size()>0)
        {
            PlayingCard card=(PlayingCard)unsorted.firstElement();
            int cardBitEncodedValue=card.getBitEncodedValue();
            for(int u=1;u<unsorted.size();u++)
            {
                PlayingCard card2=(PlayingCard)unsorted.elementAt(u);
                int card2BitEncodedValue=card2.getBitEncodedValue();
                if((card2BitEncodedValue<cardBitEncodedValue)
                ||((card2BitEncodedValue==cardBitEncodedValue)
                    &&(card2.getBitEncodedSuit()<card.getBitEncodedSuit())))
                {
                    card=card2;
                    cardBitEncodedValue=card.getBitEncodedValue();
                }
            }
            unsorted.remove(card);
            addCard(card);
        }
    }
    
    
    // sortByValueAceLow()
    // This method is a sort of anti-shuffle.  It puts the cards in
    // order, first by value, then by suit, with ace low.
    public void sortByValueAceLow()
    {
        Vector unsorted=getContents();
        if(unsorted.size()==0) return;
        // first step is to get them out of the deck and
        // then re-add them in order.
        for(int u=0;u<unsorted.size();u++)
            removeCard((PlayingCard)unsorted.elementAt(u));
        
        // now we pick them out in order
        while(unsorted.size()>0)
        {
            PlayingCard card=(PlayingCard)unsorted.firstElement();
            int cardBitEncodedValue=card.getBitEncodedValue();
            if(cardBitEncodedValue==14) cardBitEncodedValue=1;
            for(int u=1;u<unsorted.size();u++)
            {
                PlayingCard card2=(PlayingCard)unsorted.elementAt(u);
                int card2BitEncodedValue=card2.getBitEncodedValue();
                if(card2BitEncodedValue==14) card2BitEncodedValue=1;
                if((card2BitEncodedValue<cardBitEncodedValue)
                ||((card2BitEncodedValue==cardBitEncodedValue)
                    &&(card2.getBitEncodedSuit()<card.getBitEncodedSuit())))
                {
                    card=card2;
                    cardBitEncodedValue=card.getBitEncodedValue();
                    if(cardBitEncodedValue==14) cardBitEncodedValue=1;
                }
            }
            unsorted.remove(card);
            addCard(card);
        }
    }

    // createEmptyHand(Environmental player)
    // creates an empty HandOfCards object
    // if the player passed in is not null, it will
    // add the new hand to the inventory of the given 
    // hand-holder.  Either way, it will return the 
    // empty hand object.
    public HandOfCards createEmptyHand(Environmental player)
    {
        // calling this method without the intention
        // of putting a card inside is counter-productive.
        // the other methods should automatically create and
        // destroy the hands as cards are dealt and returned
        // to the deck respectively!
        HandOfCards hand=(HandOfCards)CMClass.getMiscMagic("StdHandOfCards");
        if(player instanceof MOB)
        {
            if(hand.owner()==null)
                ((MOB)player).addInventory(hand);
            else
                ((MOB)player).giveItem(hand);
            if(((MOB)player).isMine(hand))
                return hand;
            return null;
        }
        else
        if(player instanceof Room)
        {
            if(hand.owner()==null)
                ((Room)player).addItem(hand);
            else
                ((Room)player).addItemRefuse(hand,0);
            if(((Room)player).isContent(hand))
                return hand;
            return null;
        }
        return hand;
    }

    // containsCard(String cardStringCode)
    // returns whether this hand contains a card of
    // the given string code value
    // a string code is a single letter suit followed
    // by a single letter for face cards and the ace,
    // or a number for other cards.
    public boolean containsCard(String cardStringCode)
    { return getCard(cardStringCode)!=null;}
    
    // getCard(String cardStringCode)
    // returns the PlayingCard from this deck or hand if
    // it is to be found herein.  DOES NOT REMOVE!
    // removeCard should be called next to do that.
    // a string code is a single letter suit followed
    // by a single letter for face cards and the ace,
    // or a number for other cards.
    public PlayingCard getCard(String cardStringCode)
    {
        Vector handContents=getContents();
        if(handContents.size()==0) return null;
        if(cardStringCode.length()<2) return null;
        for(int i=0;i<handContents.size();i++)
        {
            PlayingCard card=(PlayingCard)handContents.elementAt(i);
            if((card.getStringEncodedSuit().charAt(0)==cardStringCode.charAt(0))
            &&card.getStringEncodedValue().equalsIgnoreCase(cardStringCode.substring(1)))
                return card;
        }
        return null;
    }
    
    // getFirstCardOfValue(String cardStringCode)
    // returns the first PlayingCard from this deck or hand
    // of the given value is to be found herein.  DOES NOT REMOVE!
    // removeCard should be called next to do that.
    // a string code is a single letter for face cards 
    // and the ace, or a number for other cards.
    public PlayingCard getFirstCardOfValue(String cardStringCode)
    {
        Vector handContents=getContents();
        if(handContents.size()==0) return null;
        if(cardStringCode.length()==0) return null;
        if(cardStringCode.length()==1) cardStringCode=" "+cardStringCode;
        for(int i=0;i<handContents.size();i++)
        {
            PlayingCard card=(PlayingCard)handContents.elementAt(i);
            if(card.getStringEncodedValue().equalsIgnoreCase(cardStringCode.substring(1)))
                return card;
        }
        return null;
    }
    
    // containsAtLeastOneOfValue(String cardStringCode)
    // returns whether a PlayingCard in this deck or hand
    // of the given value is to be found herein.  
    // a string code is a single letter for face cards 
    // and the ace, or a number for other cards.
    public boolean containsAtLeastOneOfValue(String cardStringCode)
    { return getFirstCardOfValue(cardStringCode)!=null;}
    
    // containsAtLeastOneOfSuit(String cardStringCode)
    // returns whether a PlayingCard in this deck or hand
    // of the given suit is to be found herein.  
    // a string code is a single letter suit 
    public boolean containsAtLeastOneOfSuit(String cardStringCode)
    { return getFirstCardOfSuit(cardStringCode)!=null;}
    
    // getFirstCardOfSuit(String cardStringCode)
    // returns the first PlayingCard from this deck or hand
    // of the given suit is to be found herein.  DOES NOT REMOVE!
    // removeCard should be called next to do that.
    // a string code is a single letter suit 
    public PlayingCard getFirstCardOfSuit(String cardStringCode)
    {
        Vector handContents=getContents();
        if(handContents.size()==0) return null;
        if(cardStringCode.length()==0) return null;
        for(int i=0;i<handContents.size();i++)
        {
            PlayingCard card=(PlayingCard)handContents.elementAt(i);
            if(card.getStringEncodedSuit().charAt(0)==cardStringCode.charAt(0))
                return card;
        }
        return null;
    }
    
    // containsCard(int cardBitCode)
    // returns whether this hand contains a card of
    // the given bit code value
    // a bit code is as described in PlayingCard.java
    public boolean containsCard(int cardBitCode)
    { return getCard(cardBitCode)!=null;}
    
    // getCard(int cardBitCode)
    // returns the PlayingCard from this deck or hand if
    // it is to be found herein.  DOES NOT REMOVE!
    // removeCard should be called next to do that.
    // a bit code is as described in PlayingCard.java
    public PlayingCard getCard(int cardBitCode)
    {
        if((cardBitCode&(1+2+4+8))==1)
            cardBitCode=(cardBitCode&(16+32))+14;
        Vector handContents=getContents();
        if(handContents.size()==0) return null;
        for(int i=0;i<handContents.size();i++)
        {
            PlayingCard card=(PlayingCard)handContents.elementAt(i);
            if(card.envStats().ability()==cardBitCode)
                return card;
        }
        return null;
    }
    
    // getFirstCardOfValue(int cardBitCode)
    // returns the first PlayingCard from this deck or hand
    // of the given value is to be found herein.  DOES NOT REMOVE!
    // removeCard should be called next to do that.
    // a bit code is as described in PlayingCard.java
    public PlayingCard getFirstCardOfValue(int cardBitCode)
    {
        Vector handContents=getContents();
        if(handContents.size()==0) return null;
        cardBitCode=cardBitCode&(1+2+4+8);
        if(cardBitCode==1) cardBitCode=14;
        for(int i=0;i<handContents.size();i++)
        {
            PlayingCard card=(PlayingCard)handContents.elementAt(i);
            if(card.getBitEncodedValue()==cardBitCode)
                return card;
        }
        return null;
    }
    
    // containsAtLeastOneOfValue(int cardBitCode)
    // returns whether a PlayingCard in this deck or hand
    // of the given value is to be found herein.  
    // a bit code is as described in PlayingCard.java
    public boolean containsAtLeastOneOfValue(int cardBitCode)
    { return getFirstCardOfValue(cardBitCode)!=null;}
    
    // containsAtLeastOneOfSuit(int cardBitCode)
    // returns whether a PlayingCard in this deck or hand
    // of the given suit is to be found herein.  
    // a bit code is as described in PlayingCard.java
    public boolean containsAtLeastOneOfSuit(int cardBitCode)
    { return getFirstCardOfSuit(cardBitCode)!=null;}
    
    // getFirstCardOfSuit(int cardBitCode)
    // returns the first PlayingCard from this deck or hand
    // of the given suit is to be found herein.  DOES NOT REMOVE!
    // removeCard should be called next to do that.
    // a bit code is as described in PlayingCard.java
    public PlayingCard getFirstCardOfSuit(int cardBitCode)
    {
        Vector handContents=getContents();
        if(handContents.size()==0) return null;
        cardBitCode=cardBitCode&(16+32);
        for(int i=0;i<handContents.size();i++)
        {
            PlayingCard card=(PlayingCard)handContents.elementAt(i);
            if(card.getBitEncodedSuit()==cardBitCode)
                return card;
        }
        return null;
    }
    
    // canContain(Environmental E)
    // we override the canContain method of StdContainer so
    // that we are allowed to ONLY put playing cards in the
    // container.
    public boolean canContain(Environmental E)
    {
        if (!(E instanceof PlayingCard)) return false;
        return true;
    }
        
    // this method is a general event handler
    // to make playing stud-games easier on the players,
    // we capture the LOOK event in this method and
    // then tell the looker what the other player
    // has  "turned up"
    public void executeMsg(Environmental host, CMMsg msg)
    {
        if(owner() instanceof MOB)
        {
            // check to see if this event is a LOOK or EXAMINE event 
            // and see of the target of the action is the owner of
            // this hand of cards, and also check quickly whether
            // the player HAS any cards.
            if(((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE))
            &&(msg.target()==owner())
            &&(numberOfCards()>0))
            {
                // now we build a string list of the cards which
                // are marked as FACE-UP
                StringBuffer str=new StringBuffer("");
                Vector cards=getContents();
                for(int c=0;c<cards.size();c++)
                {
                    PlayingCard card=(PlayingCard)cards.elementAt(c);
                    if(card.isFaceUp())
                        str.append(card.name()+", ");
                }
                
                // now we have that list, so we generate a new OK (always occurs)
                // message to notify the looker of what our player has.  We add our
                // new message as a "trailer" to ensure it happens after the
                // primary event has taken place.  Remember, at this point, we
                // are capturing an event in mid-stride.  This event we have
                // captured has not completed executing yet.
                if(str.length()>0)
                    msg.addTrailerMsg(CMClass.getMsg(msg.source(),owner(),null,CMMsg.MSG_OK_VISUAL,"<T-NAME> <T-HAS-HAVE> the following cards shown: "+str.toString().substring(0,str.length()-2)+".",CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null));
            }
        }
        super.executeMsg(host,msg);
    }
}