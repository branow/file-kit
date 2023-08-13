package com.branow.file.kit.dao;

import com.branow.file.kit.JUnitTest;
import com.branow.file.kit.io.DirectoryEntity;
import com.branow.file.kit.utils.FileIOUtils;
import com.branow.file.kit.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AbstractFolderDaoTest extends JUnitTest {

    public AbstractFolderDaoTest() {
        super(Path.of("src/test/java/resources/com/branow/file/kit/dao/abstract-folder-dao-test"));
    }


    @ParameterizedTest
    @MethodSource("provideConstructorNull")
    public void constructorNull(String folder, Function<ObjectDTO, Integer> getId, String fileExtension) {
        if (folder != null) {
            Path path = path(folder);
            create(path, true);
            Assertions.assertThrows(NullPointerException.class, () -> new AbstractFolderDaoImpl(new DirectoryEntity(path), getId, fileExtension));
        } else {
            Assertions.assertThrows(NullPointerException.class, () -> new AbstractFolderDaoImpl(null, getId, fileExtension));
        }
    }


    @ParameterizedTest
    @MethodSource("provideSelect")
    public void select(List<ObjectDTO> expected) {
        String name = "select";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        expected.forEach(e -> createElement(name, e));
        assertionListEquals(expected, afd.select());
    }

    @ParameterizedTest
    @MethodSource("provideSelectId")
    public void selectId(List<ObjectDTO> add, Optional<ObjectDTO> expected, int id) {
        String name = "selectId";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        add.forEach(e -> createElement(name, e));
        Assertions.assertEquals(expected, afd.select(id));
    }

    @ParameterizedTest
    @MethodSource("provideSelectPredicate")
    public void selectPredicate(List<ObjectDTO> add, List<ObjectDTO> expected, Predicate<ObjectDTO> predicate) {
        String name = "select";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        add.forEach(e -> createElement(name, e));
        assertionListEquals(expected, afd.select(predicate));
    }


    @Test
    public void insertElementWithTheSameIdExist() {
        String name = "insert";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        list().forEach(e -> createElement(name, e));
        for (ObjectDTO o : list()) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> afd.insert(o));
        }
    }

    @ParameterizedTest
    @MethodSource("provideInsert")
    public void insert(ObjectDTO object) {
        String name = "insert";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        afd.insert(object);

        Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
        Assertions.assertTrue(Files.exists(path));
        String expected = object.getKey() + " | " + object.getValue();
        String actual = new String(read(path));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void insertCollectionElementWithTheSameIdExist() {
        String name = "insertCollection";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        createElement(name, list(2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> afd.insert(list()));
        for (ObjectDTO o : list()) {
            if (o.getKey() != 2) {
                Path path = path(name + File.separator + o.getKey() + "." + afd.getFileExtension());
                Assertions.assertTrue(Files.notExists(path));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideInsertCollection")
    public void insertCollection(List<ObjectDTO> objects) {
        String name = "insert";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        afd.insert(objects);

        for (ObjectDTO object : objects) {
            Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
            Assertions.assertTrue(Files.exists(path));
            String expected = object.getKey() + " | " + object.getValue();
            String actual = new String(read(path));
            Assertions.assertEquals(expected, actual);
        }
    }


    @Test
    public void updateNoElementWithSuchId() {
        String name = "update";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        for (ObjectDTO object : list()) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> afd.update(object));
            createElement(name, object);
        }
    }

    @ParameterizedTest
    @MethodSource("provideUpdate")
    public void update(ObjectDTO object) {
        String name = "update";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        list().forEach(e -> createElement(name, e));
        afd.update(object);

        Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
        Assertions.assertTrue(Files.exists(path));
        String expected = object.getKey() + " | " + object.getValue();
        String actual = new String(read(path));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateCollectionNoElementWithSuchId() {
        String name = "update";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        List<ObjectDTO> init = List.of(list(0), list(1), list(2), list(3));
        init.forEach(e -> createElement(name, e));
        List<ObjectDTO> update = List.of(new ObjectDTO(list(0).getKey(), list(1).getValue()),
                new ObjectDTO(list(4).getKey(), list(2).getValue()));

        Assertions.assertThrows(IllegalArgumentException.class, () -> afd.update(update));

        for (ObjectDTO object : init) {
            Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
            Assertions.assertTrue(Files.exists(path));
            String expected = object.getKey() + " | " + object.getValue();
            String actual = new String(read(path));
            Assertions.assertEquals(expected, actual);
        }

        Path path = path(name + File.separator + list(4).getKey() + "." + afd.getFileExtension());
        Assertions.assertTrue(Files.notExists(path));
    }

    @ParameterizedTest
    @MethodSource("provideUpdateCollection")
    public void updateCollection(Collection<ObjectDTO> objects) {
        String name = "update";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        list().forEach(e -> createElement(name, e));
        afd.update(objects);

        for (ObjectDTO object : objects) {
            Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
            Assertions.assertTrue(Files.exists(path));
            String expected = object.getKey() + " | " + object.getValue();
            String actual = new String(read(path));
            Assertions.assertEquals(expected, actual);
        }
    }

    @ParameterizedTest
    @MethodSource("provideUpdateCondition")
    public void updateCondition(Predicate<ObjectDTO> condition, Consumer<ObjectDTO> update) {
        String name = "update";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        List<ObjectDTO> objects = list();
        objects.forEach(e -> createElement(name, e));
        afd.update(condition, update);

        objects = objects.stream().filter(condition).peek(update).toList();

        for (ObjectDTO object : objects) {
            Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
            Assertions.assertTrue(Files.exists(path));
            String expected = object.getKey() + " | " + object.getValue();
            String actual = new String(read(path));
            Assertions.assertEquals(expected, actual);
        }
    }


    @ParameterizedTest
    @MethodSource("provideRemove")
    public void remove(ObjectDTO object) {
        String name = "update";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        list().forEach(e -> createElement(name, e));
        afd.remove(object.getKey());

        Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
        Assertions.assertTrue(Files.notExists(path));
    }

    @ParameterizedTest
    @MethodSource("provideRemovePredicate")
    public void removePredicate(Predicate<ObjectDTO> predicate) {
        String name = "update";
        AbstractFolderDaoImpl afd = abstractFolderDaoImplOf(name);
        list().forEach(e -> createElement(name, e));
        afd.remove(predicate);

        for (ObjectDTO object : list().stream().filter(predicate).toList()) {
            Path path = path(name + File.separator + object.getKey() + "." + afd.getFileExtension());
            Assertions.assertTrue(Files.notExists(path));
        }
    }


    private AbstractFolderDaoImpl abstractFolderDaoImplOf(String name) {
        Path root = path(name);
        create(root, true);
        return new AbstractFolderDaoImpl(new DirectoryEntity(root),
                AbstractMap.SimpleEntry::getKey, "txt");
    }

    private void createElement(String folder, ObjectDTO objectDTO) {
        Path path = path(folder + File.separator + objectDTO.getKey() + ".txt");
        create(path);
        String text = objectDTO.getKey() + " | " + objectDTO.getValue();
        write(path, text.getBytes());
    }

    private static <T> void assertionListEquals(List<T> expected, List<T> actual) {
        Assertions.assertEquals(expected.size(), actual.size());
        for (T ex : expected)
            Assertions.assertTrue(actual.contains(ex));
        for (T ac : actual)
            Assertions.assertTrue(expected.contains(ac));
    }


    private static Stream<Arguments> provideConstructorNull() {
        return Stream.of(
                Arguments.of(null, (Function<ObjectDTO, Integer>) AbstractMap.SimpleEntry::getKey, "txt"),
                Arguments.of("dir", null, "txt"),
                Arguments.of("dir", (Function<ObjectDTO, Integer>) AbstractMap.SimpleEntry::getKey, null),
                Arguments.of(null, null, "txt"),
                Arguments.of("dir", null, null),
                Arguments.of(null, (Function<ObjectDTO, Integer>) AbstractMap.SimpleEntry::getKey, null),
                Arguments.of(null, null, null)
        );
    }

    private static Stream<Arguments> provideSelect() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(list(1))),
                Arguments.of(List.of(list(0), list(1), list(2))),
                Arguments.of(list())
        );
    }

    private static Stream<Arguments> provideSelectId() {
        return Stream.of(
                Arguments.of(List.of(), Optional.empty(), 0),
                Arguments.of(List.of(list(1)), Optional.of(list(1)), 1),
                Arguments.of(List.of(list(0), list(1), list(2)), Optional.of(list(0)), 0),
                Arguments.of(list(), Optional.of(list(3)), 3)
        );
    }

    private static Stream<Arguments> provideSelectPredicate() {
        return Stream.of(
                Arguments.of(List.of(), List.of(), (Predicate<ObjectDTO>) (e) -> true),
                Arguments.of(list(), list(), (Predicate<ObjectDTO>) (e) -> true),
                Arguments.of(list(), List.of(), (Predicate<ObjectDTO>) (e) -> false),
                Arguments.of(list(), List.of(list(4), list(5)), (Predicate<ObjectDTO>) (e) -> e.getKey() > 3),
                Arguments.of(list(), List.of(list(4)), (Predicate<ObjectDTO>) (e) -> e.getValue().contains("comment"))
        );
    }

    private static Stream<Arguments> provideInsert() {
        return Stream.of(
                Arguments.of(list(0)),
                Arguments.of(list(1)),
                Arguments.of(list(2)),
                Arguments.of(list(3)),
                Arguments.of(list(4)),
                Arguments.of(list(5))
        );
    }

    private static Stream<Arguments> provideInsertCollection() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(list(1))),
                Arguments.of(List.of(list(0), list(2))),
                Arguments.of(list())
        );
    }

    private static Stream<Arguments> provideUpdate() {
        return Stream.of(
                Arguments.of(new ObjectDTO(list(0).getKey(), list(1).getValue())),
                Arguments.of(new ObjectDTO(list(1).getKey(), list(2).getValue())),
                Arguments.of(new ObjectDTO(list(2).getKey(), list(3).getValue())),
                Arguments.of(new ObjectDTO(list(3).getKey(), list(4).getValue())),
                Arguments.of(new ObjectDTO(list(4).getKey(), list(5).getValue()))
        );
    }


    private static Stream<Arguments> provideUpdateCollection() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(new ObjectDTO(list(0).getKey(), list(1).getValue()))),
                Arguments.of(List.of(
                        new ObjectDTO(list(0).getKey(), list(1).getValue()),
                        new ObjectDTO(list(1).getKey(), list(2).getValue())
                )),
                Arguments.of(List.of(
                        new ObjectDTO(list(0).getKey(), list(1).getValue()),
                        new ObjectDTO(list(1).getKey(), list(2).getValue()),
                        new ObjectDTO(list(2).getKey(), list(3).getValue()),
                        new ObjectDTO(list(3).getKey(), list(4).getValue())
                ))
        );
    }

    private static Stream<Arguments> provideUpdateCondition() {
        return Stream.of(
                Arguments.of(
                        (Predicate<ObjectDTO>) (o) -> true,
                        (Consumer<ObjectDTO>) (o) -> o.setValue(o.getValue().toLowerCase())),
                Arguments.of(
                        (Predicate<ObjectDTO>) (o) -> false,
                        (Consumer<ObjectDTO>) (o) -> o.setValue("")),
                Arguments.of(
                        (Predicate<ObjectDTO>) (o) -> o.getKey() > 2,
                        (Consumer<ObjectDTO>) (o) -> o.setValue(o.getValue())),
                Arguments.of(
                        (Predicate<ObjectDTO>) (o) -> o.getValue().length() > 10,
                        (Consumer<ObjectDTO>) (o) -> o.setValue(o.getValue().substring(0, 10)))
        );
    }

    private static Stream<Arguments> provideRemove() {
        return Stream.of(
                Arguments.of(list(0)),
                Arguments.of(list(1)),
                Arguments.of(list(2)),
                Arguments.of(list(3)),
                Arguments.of(list(4)),
                Arguments.of(list(5))
        );
    }

    private static Stream<Arguments> provideRemovePredicate() {
        return Stream.of(
                Arguments.of((Predicate<ObjectDTO>) (e) -> true),
                Arguments.of((Predicate<ObjectDTO>) (e) -> false),
                Arguments.of((Predicate<ObjectDTO>) (e) -> e.getKey() == 2),
                Arguments.of((Predicate<ObjectDTO>) (e) -> e.getValue().contains("comment")),
                Arguments.of((Predicate<ObjectDTO>) (e) -> e.getValue().length() < 5)
        );
    }


    private static ObjectDTO list(int index) {
        return list().get(index);
    }

    private static List<ObjectDTO> list() {
        return List.of(
                new ObjectDTO(0, ""),
                new ObjectDTO(1, "Hello World"),
                new ObjectDTO(2, "Empty String"),
                new ObjectDTO(3, " target data "),
                new ObjectDTO(4, "/*comment*/"),
                new ObjectDTO(5, "<hmtl>code</html>")
        );
    }


    private static class AbstractFolderDaoImpl extends AbstractFolderDao<ObjectDTO, Integer> {

        public AbstractFolderDaoImpl(DirectoryEntity dir, Function<ObjectDTO, Integer> getId, String fileExtension) {
            super(dir, getId, fileExtension);
        }

        @Override
        protected void write(ObjectDTO o) {
            String text = o.getKey() + " | " + o.getValue();
            FileIOUtils.overwrite(path(o.getKey()), text);
        }

        @Override
        protected ObjectDTO read(Path path) {
            String[] strings = FileIOUtils.readString(path).split(" \\| ");
            if (strings.length < 2)
                return new ObjectDTO(Integer.valueOf(strings[0]), "");
            else
                return new ObjectDTO(Integer.valueOf(strings[0]), strings[1]);
        }
    }

    private static class ObjectDTO extends AbstractMap.SimpleEntry<Integer, String> {
        public ObjectDTO(Integer key, String value) {
            super(key, value);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            ObjectDTO ob = (ObjectDTO) o;
            return getKey().equals(ob.getKey()) && getValue().equals(ob.getValue());
        }
    }
}
