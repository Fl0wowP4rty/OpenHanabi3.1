package com.sun.javafx.font.freetype;

class FT_GlyphSlotRec {
   FT_Glyph_Metrics metrics = new FT_Glyph_Metrics();
   long linearHoriAdvance;
   long linearVertAdvance;
   long advance_x;
   long advance_y;
   int format;
   FT_Bitmap bitmap = new FT_Bitmap();
   int bitmap_left;
   int bitmap_top;
}
