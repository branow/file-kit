package com.branow.file.kit.utils;

import com.branow.file.kit.io.RuntimeIOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class FileIOUtilsTest {

    private static final Path resourceFolder = Path.of("src/test/java/resources/com/branow/file/kit/utils/file-utils-test/");

    @AfterEach
    public void cleanFolder() {
        try (Stream<Path> stream = Files.list(resourceFolder)) {
            List<Path> children = stream.toList();
            for (Path child : children) {
                delete(child);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void readByteBuffer() {
        ByteBuffer expected = null, actual = null;
        int pos = 0;
        int size = 0;
        byte[] bytes = src().getBytes();

        Path path = path("read.txt");
        create(path);
        write(path, src().getBytes());

        size = 450;
        expected = ByteBuffer.wrap(Arrays.copyOf(bytes, size));
        actual = ByteBuffer.allocate(size);
        FileIOUtils.read(path, actual);
        Assertions.assertEquals(expected, actual);

        size = 500;
        pos = 12;
        expected = ByteBuffer.wrap(Arrays.copyOfRange(bytes, pos, size + pos));
        actual = ByteBuffer.allocate(size);
        FileIOUtils.read(path, actual, pos);
        Assertions.assertEquals(expected, actual);

        expected = ByteBuffer.wrap(bytes);
        actual = FileIOUtils.readByteBuffer(path);
        Assertions.assertEquals(expected, actual);

        size = 151;
        expected = ByteBuffer.wrap(Arrays.copyOf(bytes, size));
        actual = FileIOUtils.readByteBuffer(path, size);
        Assertions.assertEquals(expected, actual);

        pos = 755;
        expected = ByteBuffer.wrap(Arrays.copyOfRange(bytes, pos, bytes.length));
        actual = FileIOUtils.readByteBuffer(path, (long) pos);
        Assertions.assertEquals(expected, actual);

        pos = 151;
        size = 111;
        expected = ByteBuffer.wrap(Arrays.copyOfRange(bytes, pos, pos + size));
        actual = FileIOUtils.readByteBuffer(path, pos, size);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void readBytes() {
        byte[] expected = null, actual = null;
        int pos = 0;
        int size = 0;
        byte[] bytes = src().getBytes();

        Path path = path("read.txt");
        create(path);
        write(path, bytes);

        size = 450;
        expected = Arrays.copyOf(bytes, size);
        actual = new byte[size];
        FileIOUtils.read(path, actual);
        Assertions.assertArrayEquals(expected, actual);

        size = 500;
        pos = 12;
        expected = Arrays.copyOfRange(bytes, pos, size + pos);
        actual = new byte[size];
        FileIOUtils.read(path, actual, pos);
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void readString() {
        String expected = null, actual = null;
        int pos = 0;
        int size = 0;
        String src = src();

        Path path = path("read.txt");
        create(path);
        write(path, src.getBytes());

        expected = src;
        actual = FileIOUtils.readString(path);
        Assertions.assertEquals(expected, actual);

        size = 151;
        expected = src.substring(0, size);
        actual = FileIOUtils.readStringTo(path, size);
        Assertions.assertEquals(expected, actual);

        pos = 755;
        expected = src.substring(pos);
        actual = FileIOUtils.readStringFrom(path, pos);
        Assertions.assertEquals(expected, actual);

        pos = 151;
        size = 111;
        expected = src.substring(pos, pos + size);
        actual = FileIOUtils.readStringFromTo(path, pos, size);
        Assertions.assertEquals(expected, actual);
    }


    @Test
    public void writeByteBuffer() {
        ByteBuffer expected = null, actual = null, write = null;
        int pos = 0;
        byte[] bytes = src().getBytes();

        Path path = path("write.txt");
        create(path);
        write(path, bytes);
        expected = ByteBuffer.wrap(bytes);

        write = ByteBuffer.wrap(write1().getBytes());
        FileIOUtils.write(path, write);
        expected = expected.put(0, write.array());
        actual = ByteBuffer.wrap(read(path));
        Assertions.assertEquals(expected, actual);

        pos = 245;
        write = ByteBuffer.wrap(write2().getBytes());
        FileIOUtils.write(path, write, pos);
        expected = expected.put(pos, write.array());
        actual = ByteBuffer.wrap(read(path));
        Assertions.assertEquals(expected, actual);

        write = ByteBuffer.wrap(write3().getBytes());
        FileIOUtils.overwrite(path, write);
        expected = write.flip();
        actual = ByteBuffer.wrap(read(path));
        Assertions.assertEquals(expected, actual);

        pos = 10;
        write = ByteBuffer.wrap(write4().getBytes());
        FileIOUtils.overwrite(path, write, pos);
        expected = expected.limit(pos + write.array().length);
        expected = expected.put(pos, write.array());
        actual = ByteBuffer.wrap(read(path));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void writeString() {
        String expected = null, actual = null, write = null;
        int pos = 0;

        Path path = path("write.txt");
        create(path);
        write(path, src().getBytes());
        expected = src();

        write = write1();
        FileIOUtils.write(path, write);
        expected = write + expected.substring(write.length());
        actual = new String(read(path));
        Assertions.assertEquals(expected, actual);

        pos = 245;
        write = write2();
        FileIOUtils.write(path, write, pos);
        expected = expected.substring(0, pos) + write + expected.substring(pos + write.length());
        actual = new String(read(path));
        Assertions.assertEquals(expected, actual);

        write = write3();
        FileIOUtils.overwrite(path, write);
        expected = write;
        actual = new String(read(path));
        Assertions.assertEquals(expected, actual);

        pos = 10;
        write = write4();
        FileIOUtils.overwrite(path, write, pos);
        expected = expected.substring(0, pos) + write;
        actual = new String(read(path));
        Assertions.assertEquals(expected, actual);
    }


    @Test
    public void appendByteBuffer() {
        byte[] expected = null, actual = null, write = null;
        int pos = 0;
        byte[] bytes = src().getBytes();

        Path path = path("append.txt");
        create(path);
        write(path, bytes);
        expected = bytes;

        write = write1().getBytes();
        FileIOUtils.append(path, ByteBuffer.wrap(write));
        bytes = expected;
        expected = new byte[bytes.length + write.length];
        System.arraycopy(bytes, 0, expected, 0, bytes.length);
        System.arraycopy(write, 0, expected, bytes.length, write.length);
        actual = read(path);
        Assertions.assertArrayEquals(expected, actual);

        pos = 245;
        write = write2().getBytes();
        FileIOUtils.append(path, ByteBuffer.wrap(write), pos);
        bytes = expected;
        expected = new byte[bytes.length + write.length];
        System.arraycopy(bytes, 0, expected, 0, pos);
        System.arraycopy(write, 0, expected, pos, write.length);
        System.arraycopy(bytes, pos, expected, pos + write.length, bytes.length - pos);
        actual = read(path);
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void appendString() {
        String expected = null, actual = null, write = null;
        int pos = 0;

        Path path = path("append.txt");
        create(path);
        write(path, src().getBytes());
        expected = src();

        write = write1();
        FileIOUtils.append(path, write);
        expected = expected + write;
        actual = new String(read(path));
        Assertions.assertEquals(expected, actual);

        pos = 245;
        write = write2();
        FileIOUtils.append(path, write, pos);
        expected = expected.substring(0, pos) + write + expected.substring(pos);
        actual = new String(read(path));
        Assertions.assertEquals(expected, actual);
    }


    private static void create(Path path) {
        if (Files.exists(path))
            return;
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void delete(Path path) {
        if (Files.exists(path)) {
            try {
                deleteCompletely(path);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }

    private static void deleteCompletely(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)) {
                List<Path> children = stream.toList();
                for (Path child : children) {
                    deleteCompletely(child);
                }
            }
        }
        Files.delete(path);
    }

    private static Path path(String relativePath) {
        return Path.of(resourceFolder.toString(), relativePath);
    }


    private static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Path path, byte[] bytes) {
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String write1() {
        return "Για μικρός λαϊκού κοινού των εγώ. Ζέη απαλές που της στο δεξιού θεσμών.";
    }

    private static String write2() {
        return ". 伯母さん 復讐者」 . 伯母さん 復讐者」. 伯母さん 復讐者」. 第十四章 第十七章 第十一章 第十六章";
    }

    private static String write3() {
        return "תַּחַת. פה הָאַהֲבָה מִידִידָי רץ מְיֻחָדָה פָּנֵינוּ נא אי ובהסתערות לחלומותיו. האהובה מביאים מעבודת. אתם ירח משק סוד";
    }

    private static String write4() {
        return "Последњем познијега јер тип ова поузданим ако неједнаке Сем баш пак затварати";
    }

    private static String src() {
        return "ⓐⓑⓒ ⓓⓔⓕ ⓖⓗⓘ ⓙⓚⓛ ⓜⓝⓞ ⓟⓠⓡⓢ ⓣⓤⓥ ⓦⓧⓨⓩ ⒶⒷⒸ ⒹⒺⒻ ⒼⒽⒾ ⒿⓀⓁ ⓂⓃⓄ ⓅⓆⓇⓈ ⓉⓊⓋ ⓌⓍⓎⓏ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "\uD83C\uDD50\uD83C\uDD51\uD83C\uDD52 \uD83C\uDD53\uD83C\uDD54\uD83C\uDD55 \uD83C\uDD56\uD83C\uDD57\uD83C\uDD58 \uD83C\uDD59\uD83C\uDD5A" +
                "\uD83C\uDD5B \uD83C\uDD5C\uD83C\uDD5D\uD83C\uDD5E \uD83C\uDD5F\uD83C\uDD60\uD83C\uDD61\uD83C\uDD62 \uD83C\uDD63\uD83C\uDD64\uD83C\uDD65 " +
                "\uD83C\uDD66\uD83C\uDD67\uD83C\uDD68\uD83C\uDD69 \uD83C\uDD50\uD83C\uDD51\uD83C\uDD52 \uD83C\uDD53\uD83C\uDD54\uD83C\uDD55 \uD83C\uDD56" +
                "\uD83C\uDD57\uD83C\uDD58 \uD83C\uDD59\uD83C\uDD5A\uD83C\uDD5B \uD83C\uDD5C\uD83C\uDD5D\uD83C\uDD5E \uD83C\uDD5F\uD83C\uDD60\uD83C\uDD61" +
                "\uD83C\uDD62 \uD83C\uDD63\uD83C\uDD64\uD83C\uDD65 \uD83C\uDD66\uD83C\uDD67\uD83C\uDD68\uD83C\uDD69 !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "ａｂｃ ｄｅｆ ｇｈｉ ｊｋｌ ｍｎｏ ｐｑｒｓ ｔｕｖ ｗｘｙｚ ＡＢＣ ＤＥＦ ＧＨＩ ＪＫＬ ＭＮＯ ＰＱＲＳ ＴＵＶ ＷＸＹＺ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "\uD835\uDC1A\uD835\uDC1B\uD835\uDC1C \uD835\uDC1D\uD835\uDC1E\uD835\uDC1F \uD835\uDC20\uD835\uDC21\uD835\uDC22 \uD835\uDC23\uD835\uDC24" +
                "\uD835\uDC25 \uD835\uDC26\uD835\uDC27\uD835\uDC28 \uD835\uDC29\uD835\uDC2A\uD835\uDC2B\uD835\uDC2C \uD835\uDC2D\uD835\uDC2E\uD835\uDC2F " +
                "\uD835\uDC30\uD835\uDC31\uD835\uDC32\uD835\uDC33 \uD835\uDC00\uD835\uDC01\uD835\uDC02 \uD835\uDC03\uD835\uDC04\uD835\uDC05 \uD835\uDC06\uD835" +
                "\uDC07\uD835\uDC08 \uD835\uDC09\uD835\uDC0A\uD835\uDC0B \uD835\uDC0C\uD835\uDC0D\uD835\uDC0E \uD835\uDC0F\uD835\uDC10\uD835\uDC11\uD835\uDC12" +
                " \uD835\uDC13\uD835\uDC14\uD835\uDC15 \uD835\uDC16\uD835\uDC17\uD835\uDC18\uD835\uDC19 !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "\uD835\uDD86\uD835\uDD87\uD835\uDD88 \uD835\uDD89\uD835\uDD8A\uD835\uDD8B \uD835\uDD8C\uD835\uDD8D\uD835\uDD8E \uD835\uDD8F\uD835\uDD90\uD835" +
                "\uDD91 \uD835\uDD92\uD835\uDD93\uD835\uDD94 \uD835\uDD95\uD835\uDD96\uD835\uDD97\uD835\uDD98 \uD835\uDD99\uD835\uDD9A\uD835\uDD9B \uD835" +
                "\uDD9C\uD835\uDD9D\uD835\uDD9E\uD835\uDD9F \uD835\uDD6C\uD835\uDD6D\uD835\uDD6E \uD835\uDD6F\uD835\uDD70\uD835\uDD71 \uD835\uDD72\uD835" +
                "\uDD73\uD835\uDD74 \uD835\uDD75\uD835\uDD76\uD835\uDD77 \uD835\uDD78\uD835\uDD79\uD835\uDD7A \uD835\uDD7B\uD835\uDD7C\uD835\uDD7D\uD835" +
                "\uDD7E \uD835\uDD7F\uD835\uDD80\uD835\uDD81 \uD835\uDD82\uD835\uDD83\uD835\uDD84\uD835\uDD85 !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "\uD83C\uDDE9\uD83C\uDDEA\uD83C\uDDEB \uD83C\uDDEC\uD83C\uDDED\uD83C\uDDEE \uD83C\uDDEF\uD83C\uDDF0\uD83C\uDDF1 \uD83C\uDDF2\uD83C\uDDF3" +
                "\uD83C\uDDF4 \uD83C\uDDF5\uD83C\uDDF6\uD83C\uDDF7\uD83C\uDDF8 \uD83C\uDDF9\uD83C\uDDFA\uD83C\uDDFB \uD83C\uDDFC\uD83C\uDDFD\uD83C\uDDFE" +
                "\uD83C\uDDFF \uD83C\uDDE6\uD83C\uDDE7\uD83C\uDDE8 \uD83C\uDDE9\uD83C\uDDEA\uD83C\uDDEB \uD83C\uDDEC\uD83C\uDDED\uD83C\uDDEE \uD83C\uDDEF" +
                "\uD83C\uDDF0\uD83C\uDDF1 \uD83C\uDDF2\uD83C\uDDF3\uD83C\uDDF4 \uD83C\uDDF5\uD83C\uDDF6\uD83C\uDDF7\uD83C\uDDF8 \uD83C\uDDF9\uD83C\uDDFA" +
                "\uD83C\uDDFB \uD83C\uDDFC\uD83C\uDDFD\uD83C\uDDFE\uD83C\uDDFF !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "\uD835\uDD1E\uD835\uDD1F\uD835\uDD20 \uD835\uDD21\uD835\uDD22\uD835\uDD23 \uD835\uDD24\uD835\uDD25\uD835\uDD26 \uD835\uDD27\uD835\uDD28\uD835" +
                "\uDD29 \uD835\uDD2A\uD835\uDD2B\uD835\uDD2C \uD835\uDD2D\uD835\uDD2E\uD835\uDD2F\uD835\uDD30 \uD835\uDD31\uD835\uDD32\uD835\uDD33 \uD835" +
                "\uDD34\uD835\uDD35\uD835\uDD36\uD835\uDD37 \uD835\uDD04\uD835\uDD05ℭ \uD835\uDD07\uD835\uDD08\uD835\uDD09 \uD835\uDD0Aℌℑ \uD835\uDD0D\uD835" +
                "\uDD0E\uD835\uDD0F \uD835\uDD10\uD835\uDD11\uD835\uDD12 \uD835\uDD13\uD835\uDD14ℜ\uD835\uDD16 \uD835\uDD17\uD835\uDD18\uD835\uDD19 \uD835" +
                "\uDD1A\uD835\uDD1B\uD835\uDD1Cℨ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "ₐᵦ꜀ dₑf ₉ₕᵢ ⱼₖₗ ₘₙₒ ₚqᵣₛ ₜᵤᵥ wₓᵧ₂ ₐᵦ꜀ dₑf ₉ₕᵢ ⱼₖₗ ₘₙₒ ₚqᵣₛ ₜᵤᵥ wₓᵧ₂ !\"§ $%& /₍₎ ₌?* '<> #|; ²³˷ @`´ ©«» ¤¼× {}\n" +
                "ᵃᵇᶜ ᵈᵉᶠ ᵍʰⁱ ʲᵏˡ ᵐⁿᵒ ᵖ۹ʳˢ ᵗᵘᵛ ʷˣʸᶻ ᴬᴮᑦ ᴰᴱ⸁ ᴳᴴᴵ ᴶᴷᴸ ᴹᴺᴼ ᴾ۹ᴿᔆ ᵀᵁⱽ ᵂᕽʸᙆ !\"§ $%& /⁽⁾ ⁼?* '<> #|; ²³῀ @`´ ©«» ¤¼× {}\n" +
                "⒜⒝⒞ ⒟⒠⒡ ⒢⒣⒤ ⒥⒦⒧ ⒨⒩⒪ ⒫⒬⒭⒮ ⒯⒰⒱ ⒲⒳⒴⒵ ⒜⒝⒞ ⒟⒠⒡ ⒢⒣⒤ ⒥⒦⒧ ⒨⒩⒪ ⒫⒬⒭⒮ ⒯⒰⒱ ⒲⒳⒴⒵ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "ɐqɔ pǝɟ ƃɥᴉ ɾʞl ɯuo dbɹs ʇnʌ ʍxʎz ∀\uD801\uDC12Ɔ ᗡƎℲ ⅁HI ſﻼ⅂ WNO ԀꝹᖈS ⊥ՈΛ MX⅄Z ¡\"§ $%& /() =¿* '<> #| ⸵  ²³~ @`´ ©«» ¤¼× {}\n" +
                "ɒdɔ bɘʇ ϱʜi įʞl mno qpɿƨ Ɉυv wxγz A\uD801\uDC12Ɔ ႧƎꟻ ӘHI Ⴑﻼ⅃ MИO ꟼϘЯƧ TUV WXYZ !\"§ $%& /() =⸮* '<> #| ⁏ ²³~ @`´ ©«» ¤¼× {}\n" +
                "áb́ć d́éf́ ǵh́í j́ḱĺ ḿńό ṕq́ŕś t́úv́ ẃx́ýź ÁB́Ć D́ÉF́ ǴH́Í J́ḰĹ ḾŃÓ ṔQ́ŔŚ T́ÚV́ ẂX́ÝŹ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "a̤b̤c̤ d̤e̤f̤ g̤h̤i̤ j̤k̤l̤ m̤n̤o̤ p̤q̤r̤s̤ t̤ṳv̤ w̤x̤y̤z̤ A̤B̤C̤ D̤E̤F̤ G̤H̤I̤ J̤K̤L̤ M̤N̤O̤ P̤Q̤R̤S̤ T̤ṲV̤ W̤X̤Y̤Z̤ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "äb̈c̈ d̈ëf̈ g̈ḧï j̈k̈l̈ m̈n̈ö p̈q̈r̈s̈ ẗüv̈ ẅẍÿz̈ ÄB̈C̈ D̈ЁF̈ G̈ḦЇ J̈K̈L̈ M̈N̈Ö P̈Q̈R̈S̈ T̈ÜV̈ ẄẌŸZ̈ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "ä̤b̤̈c̤̈ d̤̈ë̤f̤̈ g̤̈ḧ̤ï̤ j̤̈k̤̈l̤̈ m̤̈n̤̈ö̤ p̤̈q̤̈r̤̈s̤̈ ẗ̤ṳ̈v̤̈ ẅ̤ẍ̤ÿ̤z̤̈ Ä̤B̤̈C̤̈ D̤̈Ë̤F̤̈ G̤̈Ḧ̤Ï̤ J̤̈K̤̈L̤̈ M̤̈N̤̈Ö̤ P̤̈Q̤̈R̤̈S̤̈ T̤̈Ṳ̈V̤̈ Ẅ̤Ẍ̤Ÿ̤Z̤̈ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "̸a̸b̸c ̸d̸e̸f ̸g̸h̸i ̸j̸k̸l ̸m̸n̸o ̸p̸q̸r̸s ̸t̸u̸v ̸w̸x̸y̸z ̸A̸B̸C ̸D̸E̸F ̸G̸H̸I ̸J̸K̸L ̸M̸N̸O ̸P̸Q̸R̸S ̸T̸U̸V ̸W̸X̸Y̸Z !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "ልጌር ዕቿቻ ኗዘጎ ጋጕረ ጠክዐ የዒዪነ ፕሁሀ ሠሸሃጊ ልጌር ዕቿቻ ኗዘጎ ጋጕረ ጠክዐ የዒዪነ ፕሁሀ ሠሸሃጊ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "ﾑ乃c d乇ｷ gんﾉ ﾌズﾚ ﾶ刀o ｱq尺丂 ｲu√ wﾒﾘ乙 ﾑ乃c d乇ｷ gんﾉ ﾌズﾚ ﾶ刀o ｱq尺丂 ｲu√ wﾒﾘ乙 !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n" +
                "abc def ghi jkl mno pqrs tuv wxyz ABC DEF GHI JKL MNO PQRS TUV WXYZ !\"§ $%& /() =?* '<> #|; ²³~ @`´ ©«» ¤¼× {}\n";
    }

}
