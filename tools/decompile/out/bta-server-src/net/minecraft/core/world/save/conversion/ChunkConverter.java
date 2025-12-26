package net.minecraft.core.world.save.conversion;

public class ChunkConverter {
   public int oldSaveVersion;
   public int newSaveVersion;
   protected ChunkConverter.MappingBase[] mappings;
   public static ChunkConverter[] converters = new ChunkConverter[4];

   public ChunkConverter(int oldSaveVersion, int newSaveVersion, int maxOldId) {
      this.oldSaveVersion = oldSaveVersion;
      this.newSaveVersion = newSaveVersion;
      this.mappings = new ChunkConverter.MappingBase[maxOldId + 1];
   }

   public void addMapping(ChunkConverter.MappingBase mapping) {
      this.mappings[mapping.id] = mapping;
   }

   public void convertBlocksAndMetadata(short[] blocks, byte[] metadata) {
      for (int i = 0; i < blocks.length; i++) {
         if (blocks[i] >= 0) {
            ChunkConverter.MappingBase mapping = this.mappings[blocks[i]];
            if (mapping == null) {
               blocks[i] = 0;
               metadata[i] = 0;
            } else {
               int[] conversion = mapping.getConversion(metadata[i]);
               blocks[i] = (short)conversion[0];
               metadata[i] = (byte)conversion[1];
            }
         }
      }
   }

   static {
      converters[0] = new ChunkConverter(19132, 19133, 148);
      converters[0].addMapping(new ChunkConverter.MappingSimple(1, 1));
      converters[0].addMapping(new ChunkConverter.MappingSimple(2, 200));
      converters[0].addMapping(new ChunkConverter.MappingSimple(3, 220));
      converters[0].addMapping(new ChunkConverter.MappingSimple(4, 10));
      converters[0].addMapping(new ChunkConverter.MappingSimple(5, 50));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(6, new int[]{310, 312, 313, 314}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(7, 260));
      converters[0].addMapping(new ChunkConverter.MappingSimple(8, 270));
      converters[0].addMapping(new ChunkConverter.MappingSimple(9, 271));
      converters[0].addMapping(new ChunkConverter.MappingSimple(10, 272));
      converters[0].addMapping(new ChunkConverter.MappingSimple(11, 273));
      converters[0].addMapping(new ChunkConverter.MappingSimple(12, 250));
      converters[0].addMapping(new ChunkConverter.MappingSimple(13, 251));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(14, new int[]{370, 371, 372, 373}));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(15, new int[]{360, 361, 362, 363}));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(16, new int[]{350, 351, 352, 353}));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(17, new int[]{280, 281, 282, 283}));
      converters[0].addMapping(new ChunkConverter.MappingLeavesMetaToId(18, new int[]{290, 292, 293, 294}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(19, 230));
      converters[0].addMapping(new ChunkConverter.MappingSimple(20, 190));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(21, new int[]{380, 381, 382, 383}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(22, 433));
      converters[0].addMapping(new ChunkConverter.MappingSimple(23, 560));
      converters[0].addMapping(new ChunkConverter.MappingSimple(24, 30));
      converters[0].addMapping(new ChunkConverter.MappingSimple(25, 530));
      converters[0].addMapping(new ChunkConverter.MappingSimple(26, 610));
      converters[0].addMapping(new ChunkConverter.MappingSimple(27, 541));
      converters[0].addMapping(new ChunkConverter.MappingSimple(28, 542));
      converters[0].addMapping(new ChunkConverter.MappingSimple(29, 521));
      converters[0].addMapping(new ChunkConverter.MappingSimple(30, 620));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(31, new int[]{322, 320, 321}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(32, 322));
      converters[0].addMapping(new ChunkConverter.MappingSimple(33, 520));
      converters[0].addMapping(new ChunkConverter.MappingSimple(34, 522));
      converters[0].addMapping(new ChunkConverter.MappingSimple(35, 110));
      converters[0].addMapping(new ChunkConverter.MappingSimple(36, 523));
      converters[0].addMapping(new ChunkConverter.MappingSimple(37, 330));
      converters[0].addMapping(new ChunkConverter.MappingSimple(38, 331));
      converters[0].addMapping(new ChunkConverter.MappingSimple(39, 340));
      converters[0].addMapping(new ChunkConverter.MappingSimple(40, 341));
      converters[0].addMapping(new ChunkConverter.MappingSimple(41, 432));
      converters[0].addMapping(new ChunkConverter.MappingSimple(42, 431));
      converters[0].addMapping(new ChunkConverter.MappingComplex(43, new int[]{144, 142, 140, 141, 143}, new int[]{1, 1, 1, 1, 1}));
      converters[0].addMapping(new ChunkConverter.MappingComplex(44, new int[]{144, 142, 140, 141, 143}, new int[]{0, 0, 0, 0, 0}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(45, 120));
      converters[0].addMapping(new ChunkConverter.MappingSimple(46, 500));
      converters[0].addMapping(new ChunkConverter.MappingSimple(47, 100));
      converters[0].addMapping(new ChunkConverter.MappingSimple(48, 11));
      converters[0].addMapping(new ChunkConverter.MappingSimple(49, 180));
      converters[0].addMapping(new ChunkConverter.MappingSimple(50, 60));
      converters[0].addMapping(new ChunkConverter.MappingSimple(51, 630));
      converters[0].addMapping(new ChunkConverter.MappingSimple(52, 640));
      converters[0].addMapping(new ChunkConverter.MappingSimple(53, 160));
      converters[0].addMapping(new ChunkConverter.MappingSimple(54, 680));
      converters[0].addMapping(new ChunkConverter.MappingSimple(55, 450));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(56, new int[]{410, 411, 412, 413}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(57, 435));
      converters[0].addMapping(new ChunkConverter.MappingSimple(58, 650));
      converters[0].addMapping(new ChunkConverter.MappingSimple(59, 690));
      converters[0].addMapping(new ChunkConverter.MappingSimple(60, 700));
      converters[0].addMapping(new ChunkConverter.MappingSimple(61, 660));
      converters[0].addMapping(new ChunkConverter.MappingSimple(62, 661));
      converters[0].addMapping(new ChunkConverter.MappingSimple(63, 710));
      converters[0].addMapping(new ChunkConverter.MappingSimple(64, 590));
      converters[0].addMapping(new ChunkConverter.MappingSimple(65, 70));
      converters[0].addMapping(new ChunkConverter.MappingSimple(66, 540));
      converters[0].addMapping(new ChunkConverter.MappingSimple(67, 161));
      converters[0].addMapping(new ChunkConverter.MappingSimple(68, 711));
      converters[0].addMapping(new ChunkConverter.MappingSimple(69, 480));
      converters[0].addMapping(new ChunkConverter.MappingSimple(70, 490));
      converters[0].addMapping(new ChunkConverter.MappingSimple(71, 592));
      converters[0].addMapping(new ChunkConverter.MappingSimple(72, 491));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(73, new int[]{390, 391, 392, 393}));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(74, new int[]{400, 401, 402, 403}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(75, 460));
      converters[0].addMapping(new ChunkConverter.MappingSimple(76, 461));
      converters[0].addMapping(new ChunkConverter.MappingSimple(77, 470));
      converters[0].addMapping(new ChunkConverter.MappingSimple(78, 720));
      converters[0].addMapping(new ChunkConverter.MappingSimple(79, 730));
      converters[0].addMapping(new ChunkConverter.MappingSimple(80, 740));
      converters[0].addMapping(new ChunkConverter.MappingSimple(81, 750));
      converters[0].addMapping(new ChunkConverter.MappingSimple(82, 760));
      converters[0].addMapping(new ChunkConverter.MappingSimple(83, 770));
      converters[0].addMapping(new ChunkConverter.MappingSimple(84, 780));
      converters[0].addMapping(new ChunkConverter.MappingSimple(85, 80));
      converters[0].addMapping(new ChunkConverter.MappingSimple(86, 791));
      converters[0].addMapping(new ChunkConverter.MappingSimple(87, 800));
      converters[0].addMapping(new ChunkConverter.MappingSimple(88, 810));
      converters[0].addMapping(new ChunkConverter.MappingSimple(89, 820));
      converters[0].addMapping(new ChunkConverter.MappingSimple(90, 830));
      converters[0].addMapping(new ChunkConverter.MappingSimple(91, 792));
      converters[0].addMapping(new ChunkConverter.MappingSimple(92, 840));
      converters[0].addMapping(new ChunkConverter.MappingSimple(93, 510));
      converters[0].addMapping(new ChunkConverter.MappingSimple(94, 511));
      converters[0].addMapping(new ChunkConverter.MappingSimple(96, 570));
      converters[0].addMapping(new ChunkConverter.MappingSimple(97, 121));
      converters[0].addMapping(new ChunkConverter.MappingSimple(98, 122));
      converters[0].addMapping(new ChunkConverter.MappingSimple(99, 123));
      converters[0].addMapping(new ChunkConverter.MappingSimple(100, 124));
      converters[0].addMapping(new ChunkConverter.MappingSimple(101, 125));
      converters[0].addMapping(new ChunkConverter.MappingSimple(102, 210));
      converters[0].addMapping(new ChunkConverter.MappingSimple(103, 162));
      converters[0].addMapping(new ChunkConverter.MappingSimple(104, 91));
      converters[0].addMapping(new ChunkConverter.MappingSimple(105, 550));
      converters[0].addMapping(new ChunkConverter.MappingSimple(106, 600));
      converters[0].addMapping(new ChunkConverter.MappingSimple(107, 2));
      converters[0].addMapping(new ChunkConverter.MappingSimple(108, 3));
      converters[0].addMapping(new ChunkConverter.MappingSimple(109, 4));
      converters[0].addMapping(new ChunkConverter.MappingMetaToId(110, new int[]{240, 241, 242, 243}));
      converters[0].addMapping(new ChunkConverter.MappingSimple(111, 420));
      converters[0].addMapping(new ChunkConverter.MappingSimple(112, 662));
      converters[0].addMapping(new ChunkConverter.MappingSimple(113, 663));
      converters[0].addMapping(new ChunkConverter.MappingSimple(114, 437));
      converters[0].addMapping(new ChunkConverter.MappingSimple(115, 231));
      converters[0].addMapping(new ChunkConverter.MappingSimple(116, 492));
      converters[0].addMapping(new ChunkConverter.MappingSimple(117, 12));
      converters[0].addMapping(new ChunkConverter.MappingSimple(118, 13));
      converters[0].addMapping(new ChunkConverter.MappingSimple(119, 14));
      converters[0].addMapping(new ChunkConverter.MappingSimple(120, 126));
      converters[0].addMapping(new ChunkConverter.MappingSimple(121, 127));
      converters[0].addMapping(new ChunkConverter.MappingSimple(122, 128));
      converters[0].addMapping(new ChunkConverter.MappingSimple(123, 500));
      converters[0].addMapping(new ChunkConverter.MappingSimple(124, 501));
      converters[0].addMapping(new ChunkConverter.MappingSimple(125, 140));
      converters[0].addMapping(new ChunkConverter.MappingSimple(126, 141));
      converters[0].addMapping(new ChunkConverter.MappingSimple(127, 142));
      converters[0].addMapping(new ChunkConverter.MappingSimple(128, 143));
      converters[0].addMapping(new ChunkConverter.MappingSimple(129, 144));
      converters[0].addMapping(new ChunkConverter.MappingSimple(130, 146));
      converters[0].addMapping(new ChunkConverter.MappingSimple(131, 591));
      converters[0].addMapping(new ChunkConverter.MappingSimple(132, 593));
      converters[0].addMapping(new ChunkConverter.MappingSimple(134, 721));
      converters[0].addMapping(new ChunkConverter.MappingSimple(135, 831));
      converters[0].addMapping(new ChunkConverter.MappingSimple(136, 201));
      converters[0].addMapping(new ChunkConverter.MappingSimple(137, 291));
      converters[0].addMapping(new ChunkConverter.MappingSimple(138, 671));
      converters[0].addMapping(new ChunkConverter.MappingSimple(139, 790));
      converters[0].addMapping(new ChunkConverter.MappingSimple(140, 670));
      converters[0].addMapping(new ChunkConverter.MappingSimple(141, 801));
      converters[0].addMapping(new ChunkConverter.MappingSimple(142, 5));
      converters[0].addMapping(new ChunkConverter.MappingSimple(143, 20));
      converters[0].addMapping(new ChunkConverter.MappingSimple(144, 129));
      converters[0].addMapping(new ChunkConverter.MappingSimple(145, 163));
      converters[0].addMapping(new ChunkConverter.MappingSimple(146, 145));
      converters[0].addMapping(new ChunkConverter.MappingSimple(147, 222));
      converters[0].addMapping(new ChunkConverter.MappingSimple(148, 221));
   }

   protected abstract static class MappingBase {
      public int id;

      public MappingBase(int id) {
         this.id = id;
      }

      public abstract int[] getConversion(int var1);
   }

   protected static class MappingComplex extends ChunkConverter.MappingBase {
      protected int[] metaIds;
      protected int[] metaMetas;
      protected int backup;

      public MappingComplex(int oldId, int[] metaIds, int[] metaMetas) {
         this(oldId, metaIds, metaMetas, 0);
      }

      public MappingComplex(int oldId, int[] metaIds, int[] metaMetas, int backup) {
         super(oldId);
         if (metaIds.length != metaMetas.length) {
            throw new IllegalArgumentException();
         } else {
            this.metaIds = metaIds;
            this.metaMetas = metaMetas;
            this.backup = backup;
         }
      }

      @Override
      public int[] getConversion(int metadata) {
         return metadata > this.metaIds.length ? new int[]{this.backup, 0} : new int[]{this.metaIds[metadata], this.metaMetas[metadata]};
      }
   }

   protected static class MappingLeavesMetaToId extends ChunkConverter.MappingBase {
      protected int[] metaIds;
      protected int fallback;

      public MappingLeavesMetaToId(int oldId, int[] metaIds) {
         this(oldId, metaIds, 0);
      }

      public MappingLeavesMetaToId(int oldId, int[] metaIds, int fallback) {
         super(oldId);
         this.metaIds = metaIds;
         this.fallback = fallback;
      }

      @Override
      public int[] getConversion(int metadata) {
         return (metadata & 3) >= this.metaIds.length ? new int[]{this.fallback, 0} : new int[]{this.metaIds[metadata & 3], metadata & 12};
      }
   }

   protected static class MappingMetaToId extends ChunkConverter.MappingBase {
      protected int[] metaIds;
      protected int fallback;

      public MappingMetaToId(int oldId, int[] metaIds) {
         this(oldId, metaIds, 0);
      }

      public MappingMetaToId(int oldId, int[] metaIds, int fallback) {
         super(oldId);
         this.metaIds = metaIds;
         this.fallback = fallback;
      }

      @Override
      public int[] getConversion(int metadata) {
         return metadata >= this.metaIds.length ? new int[]{this.fallback, 0} : new int[]{this.metaIds[metadata], 0};
      }
   }

   protected static class MappingSimple extends ChunkConverter.MappingBase {
      protected int newId;

      public MappingSimple(int oldId, int newId) {
         super(oldId);
         this.newId = newId;
      }

      @Override
      public int[] getConversion(int metadata) {
         return new int[]{this.newId, metadata};
      }
   }
}
