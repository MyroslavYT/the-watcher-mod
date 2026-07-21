package com.thewatcher.event;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
public class BookFactory {

    private static String j(String t){
        // Escape for JSON inside Minecraft book NBT
        String s=t.replace("\\","\\\\").replace("\"","\\\"");
        return "{\"text\":\""+s+"\"}";
    }

    public static ItemStack makeBook(int id, ServerPlayerEntity player){
        return makeBook(id,player,false);
    }

    public static ItemStack makeBook(int id, ServerPlayerEntity player, boolean altered){
        ItemStack book=new ItemStack(Items.WRITTEN_BOOK);
        NbtCompound nbt=new NbtCompound();
        nbt.putBoolean("resolved",true);
        nbt.putInt("watcher_book_id",id);
        nbt.putInt("watcher_read_count",altered?1:0);
        String[] titleAuthor=getTitleAuthor(id,player,altered);
        nbt.putString("title",titleAuthor[0]);
        nbt.putString("author",titleAuthor[1]);
        NbtList pages=new NbtList();
        for(String page:getPages(id,player,altered)) pages.add(NbtString.of(j(page)));
        nbt.put("pages",pages);
        book.setNbt(nbt);
        return book;
    }

    private static String[] getTitleAuthor(int id, ServerPlayerEntity p, boolean alt){
        String pname=p!=null?p.getName().getString():"unknown";
        if(alt&&id==7) return new String[]{"Book Seven",pname};
        return new String[]{"Book "+toWord(id),"unknown"};
    }
    private static String toWord(int i){String[]w={"","One","Two","Three","Four","Five","Six","Seven"};return i<w.length?w[i]:String.valueOf(i);}

    private static String[] getPages(int id, ServerPlayerEntity p, boolean alt){
        String pname=p!=null?p.getName().getString():"[player]";
        switch(id){
            case 1: return alt?book1alt(pname):book1();
            case 2: return alt?book2alt(pname):book2();
            case 3: return alt?book3alt(pname):book3();
            case 4: return alt?book4alt(pname):book4();
            case 5: return alt?book5alt(pname):book5();
            case 6: return book6();
            case 7: return alt?new String[]{""}:book7(pname);
            default: return new String[]{""};
        }
    }

    // BOOK 1 ────────────────────────────────────────────
    static String[] book1(){return new String[]{
        "I found the mod on a forum. No username on the post. Just a link and one sentence: try it alone first.",
        "I thought it was a Herobrine mod. Cheap scares. I almost didn't download it.",
        "First two days were normal. I built a house near a river. Got some food. Nothing happened.",
        "Then on the third morning there was a sign outside my door. I hadn\'t placed it.",
        "The sign said: don\'t go to sleep.",
        "I laughed. I actually laughed out loud. Then I went to sleep in my in-game bed.",
        "When I woke up at dawn there was a second sign on top of my bed. Inside the house. The door had been closed.",
        "The second sign said: i told you."
    };}
    static String[] book1alt(String p){return new String[]{
        p+" found the mod on a forum. No username on the post.",
        p+" thought it was a Herobrine mod.",
        "The first two days were normal. "+p+" built a house near a river.",
        "Then on the third morning there was a sign outside. "+p+" hadn\'t placed it.",
        "The sign said: don\'t go to sleep.",
        p+" laughed. Then "+p+" went to sleep.",
        "When "+p+" woke up there was a second sign on top of the bed. Inside the house.",
        "The second sign said: i told you."
    };}

    // BOOK 2 ────────────────────────────────────────────
    static String[] book2(){return new String[]{
        "I started seeing it at night. Just at the very edge of render distance. A shape that didn\'t belong.",
        "Too tall. Standing completely still. Not like a mob. Just facing me.",
        "I walked toward it once. It was gone before I got halfway. Nothing there.",
        "I checked the player list. There was a name there. Just symbols. Then it was gone.",
        "When I logged back in the sign had changed. It now said: i saw you log off.",
        "I took a screenshot. When I looked at it later the figure was standing directly behind my character.",
        "I had been facing the sign. It had been right at my back. I hadn\'t turned around.",
        "I should have stopped then. I really should have just stopped right there."
    };}
    static String[] book2alt(String p){return new String[]{
        p+" started seeing it at night. Just at the edge of render distance.",
        "Too tall. Standing still. Not like a mob. Just facing "+p+".",
        p+" walked toward it once. It was gone before "+p+" got close.",
        "The player list had a name. Just symbols. Then it was gone.",
        "When "+p+" logged back in the sign said: i saw you log off.",
        p+" took a screenshot. The figure was behind "+p+".",
        p+" had been facing the sign. It had been right at "+p+"\'s back.",
        p+" should have stopped then."
    };}

    // BOOK 3 ────────────────────────────────────────────
    static String[] book3(){return new String[]{
        "I called Marcus. I thought having someone else there would make it smaller.",
        "He came over on Friday. We loaded the world together.",
        "He saw the figure within the first ten minutes. Standing past the eastern tree line.",
        "That pale red face barely visible through the trunks. Marcus leaned forward.",
        "He said: okay that\'s actually unsettling. And laughed. Then he reached past me to move the camera.",
        "And it looked at us. The figure turned to face the screen directly. Not the character. The screen.",
        "Marcus stopped laughing.",
        "He said don\'t play it without me when he left. That was the last thing he ever sent me."
    };}
    static String[] book3alt(String p){return new String[]{
        p+" called Marcus.",
        "Marcus came over on Friday. They loaded the world together.",
        "Marcus saw the figure within ten minutes. Standing past the eastern tree line.",
        "That red face through the trunks. Marcus leaned forward.",
        "Marcus said: okay that\'s actually unsettling.",
        "And it looked at them. The figure turned to face the screen.",
        "Marcus stopped laughing.",
        "He said don\'t play it without me. "+p+" played it without him."
    };}

    // BOOK 4 ────────────────────────────────────────────
    static String[] book4(){return new String[]{
        "Marcus didn\'t reply to my messages. I wasn\'t immediately worried. He went quiet sometimes.",
        "On the third day I loaded the world. There was a new sign outside my base.",
        "It said: he looked.",
        "I called Marcus. Voicemail. Called three more times. Nothing. His roommate said Marcus hadn\'t come home.",
        "His bed was unslept in. His phone was on the nightstand. His wallet was on the counter.",
        "A report had been filed.",
        "Then I opened the game. I don\'t know why. In the player list at the very bottom was one entry.",
        "It said: Marcus. Not corrupted. His actual name. Exactly as he\'d spelled it."
    };}
    static String[] book4alt(String p){return new String[]{
        "Marcus didn\'t reply to "+p+"\'s messages.",
        "On the third day "+p+" loaded the world. A new sign outside the base.",
        "It said: he looked.",
        p+" called Marcus. Voicemail. Three more times. Nothing.",
        "His bed was unslept in. His phone on the nightstand. His wallet on the counter.",
        "A report had been filed.",
        "Then "+p+" opened the game. In the player list at the very bottom.",
        "It said: Marcus. Not corrupted. His actual name."
    };}

    // BOOK 5 ────────────────────────────────────────────
    static String[] book5(){ return new String[]{
        "I went back because I needed to know. You are reading this because you needed to know too.",
        "I found a chest I hadn\'t placed near the eastern tree line. Inside was a single page.",
        "Just a list of two coordinates.",
        "I recognised the area. North, toward the mountains. I went there.",
        "In the clearing between two ridges there was a hole. 1 block wide. Going straight down.",
        "I dropped a torch. I watched it fall. It did not land.",
        "In front of the hole there was a sign. It said: don\'t look.",
        "If you have not yet been to the hole - close the game. If you have already been there, closing doesn\'t help."
    };}
    static String[] book5alt(String p){return new String[]{
        p+" went back because "+p+" needed to know.",
        "A chest. Inside was a single page. Two coordinates.",
        "The coordinates pointed north. Toward the mountains.",
        "In the clearing there was a hole. 1 block wide. Going straight down.",
        "A torch dropped in. It did not land.",
        "In front of the hole a sign. It said: don\'t look.",
        "The numbers in the player name are coordinates. NoobPlayer + X + Z. That is where the hole is.",
        "If you have not been there yet - do not go. If you have - it already knows."
    };}

    // BOOK 6 - police report ─────────────────────────────
    static String[] book6(){return new String[]{
        "INCIDENT REPORT. Case Reference: [REDACTED]. Reporting Officer: [REDACTED].",
        "Deceased: Male, 16 years of age. Name withheld. Location: residential bedroom.",
        "Found by mother at 06:42am. Light under bedroom door had been on all night.",
        "Deceased found seated at computer. No signs of external trauma. No forced entry.",
        "Computer still running. A block-based game on screen. Handwritten note on the keyboard.",
        "The note read: don\'t look.",
        "Neighbour: it didn\'t sound like a person screaming. Like something trying to sound like a person.",
        "Every mirror in the house turned to face the wall. Every painting in the bedroom also facing the wall. Case remains open."
    };}

    // BOOK 7 - final ─────────────────────────────────────
    static String[] book7(String p){return new String[]{
        "You found all seven. Every time you found one you kept going.",
        "This isn\'t a mod. It\'s a door someone left open a very long time ago.",
        "Occasionally someone walks through it by accident. Occasionally something on the other side notices.",
        "Open your tab menu right now. Scroll to the very bottom.",
        "Below all the corrupted names. Below the symbols. Below Marcus.",
        "That is where it puts the ones it has already decided about.",
        "The numbers in the fake player name are coordinates. That is where the hole is. That is where you need to go.",
        "Don\'t look into the hole. I know you are going to look into the hole. I\'m sorry. - "+p
    };}
}
