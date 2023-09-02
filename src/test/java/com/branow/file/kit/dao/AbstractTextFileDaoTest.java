package com.branow.file.kit.dao;

import com.branow.file.kit.JUnitTest;
import com.branow.file.kit.io.DirectoryEntity;
import com.branow.file.kit.io.TextFile;
import com.branow.file.kit.utils.FileIOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTextFileDaoTest extends JUnitTest {


    public AbstractTextFileDaoTest() {
        super(Path.of("src/test/java/resources/com/branow/file/kit/dao/abstract-text-file-dao-test"));
    }


    @ParameterizedTest
    @MethodSource("provideSelect")
    public void select(List<ObjectDTO> expected) {
        Path path = path("select.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        String data = expected.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        Assertions.assertEquals(expected, dao.select());
    }

    @ParameterizedTest
    @MethodSource("provideSelectId")
    public void selectId(List<ObjectDTO> add, Optional<ObjectDTO> expected, int id) {
        Path path = path("selectId.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        String data = add.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        Assertions.assertEquals(expected, dao.select(id));
    }

    @ParameterizedTest
    @MethodSource("provideSelectPredicate")
    public void selectPredicate(List<ObjectDTO> add, List<ObjectDTO> expected, Predicate<ObjectDTO> predicate) {
        Path path = path("selectId.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        String data = add.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        Assertions.assertEquals(expected, dao.select(predicate));
    }


    @Test
    public void insertElementWithTheSameIdExist() {
        Path path = path("insert.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        String data = list().stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        for (ObjectDTO o : list()) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> dao.insert(o));
        }
    }

    @ParameterizedTest
    @MethodSource("provideInsert")
    public void insert(ObjectDTO object) {
        Path path = path("insert.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        dao.insert(object);

        String expected = object.getKey() + " | " + object.getValue();
        String actual = new String(read(path));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void insertCollectionElementWithTheSameIdExist() {
        Path path = path("insertCollection.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        String data = list(2).toString();
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        Assertions.assertThrows(IllegalArgumentException.class, () -> dao.insert(list()));

        String actual = new String(read(path));
        Assertions.assertEquals(data, actual);
    }

    @ParameterizedTest
    @MethodSource("provideInsertCollection")
    public void insertCollection(List<ObjectDTO> objects) {
        Path path = path("insertCollection.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);
        dao.insert(objects);

        List<ObjectDTO> actual = null;
        if (objects.isEmpty()) {
            actual = List.of();
        } else {
            actual = Arrays.stream(new String(read(path)).split(dao.elementSeparator))
                    .map(converter()::fromString).toList();
        }


        Assertions.assertEquals(objects, actual);
    }


    @Test
    public void updateNoElementWithSuchId() {
        Path path = path("update.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);
        for (ObjectDTO object : list()) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> dao.update(object));
            dao.insert(object);
        }
    }

    @ParameterizedTest
    @MethodSource("provideUpdate")
    public void update(ObjectDTO object) {
        Path path = path("update.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        List<ObjectDTO> initial = list();
        String data = initial.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        dao.update(object);

        List<ObjectDTO> expected = initial.stream().map(e -> {
            if (Objects.equals(e.getKey(), object.getKey()))
                return object;
            else
                return e;
        }).toList();
        List<ObjectDTO> actual = Arrays.stream(new String(read(path)).split(dao.elementSeparator))
                .map(converter()::fromString).toList();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateCollectionNoElementWithSuchId() {
        Path path = path("update.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        List<ObjectDTO> init = List.of(list(0), list(1), list(2), list(3));
        List<ObjectDTO> update = List.of(new ObjectDTO(list(0).getKey(), list(1).getValue()),
                new ObjectDTO(list(4).getKey(), list(2).getValue()));

        String data = init.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        List<ObjectDTO> actual = Arrays.stream(new String(read(path)).split(dao.elementSeparator))
                .map(converter()::fromString).toList();

        Assertions.assertThrows(IllegalArgumentException.class, () -> dao.update(update));
        Assertions.assertEquals(init, actual);
    }

    @ParameterizedTest
    @MethodSource("provideUpdateCollection")
    public void updateCollection(Collection<ObjectDTO> objects) {
        Path path = path("update.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        List<ObjectDTO> initial = list();
        String data = initial.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        dao.update(objects);

        List<ObjectDTO> expected = initial.stream().map(e -> {
            Optional<ObjectDTO> op = objects.stream().filter(o -> o.getKey().equals(e.getKey())).findAny();
            return op.orElse(e);
        }).toList();
        List<ObjectDTO> actual = Arrays.stream(new String(read(path)).split(dao.elementSeparator))
                .map(converter()::fromString).toList();
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideUpdateCondition")
    public void updateCondition(Predicate<ObjectDTO> condition, Consumer<ObjectDTO> update) {
        Path path = path("update.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        List<ObjectDTO> initial = list();
        String data = initial.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        dao.update(condition, update);

        List<ObjectDTO> expected = initial.stream().peek(e -> {
            if (condition.test(e))
                update.accept(e);
        }).toList();
        List<ObjectDTO> actual = Arrays.stream(new String(read(path)).split(dao.elementSeparator))
                .map(converter()::fromString).toList();
        Assertions.assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideRemove")
    public void remove(ObjectDTO object) {
        Path path = path("remove.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        List<ObjectDTO> initial = list();
        String data = initial.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        dao.remove(object.getKey());

        List<ObjectDTO> expected = initial.stream().filter(e -> !e.getKey().equals(object.getKey())).toList();
        List<ObjectDTO> actual = Arrays.stream(new String(read(path)).split(dao.elementSeparator))
                .map(converter()::fromString).toList();
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideRemovePredicate")
    public void removePredicate(Predicate<ObjectDTO> predicate) {
        Path path = path("remove.txt");
        create(path);
        AbstractTextFileDao<ObjectDTO, Integer> dao = instant(path);

        List<ObjectDTO> initial = list();
        String data = initial.stream().map(ObjectDTO::toString).collect(Collectors.joining("\n"));
        FileIOUtils.write(path, data, StandardCharsets.UTF_8);

        dao.remove(predicate);

        List<ObjectDTO> expected = initial.stream().filter(predicate.negate()).toList();
        String read = new String(read(path));
        List<ObjectDTO> actual = null;
        if (read.isEmpty()) {
            actual = List.of();
        } else {
            actual = Arrays.stream(read.split(dao.elementSeparator))
                    .map(converter()::fromString).toList();
        }
        Assertions.assertEquals(expected, actual);
    }


    protected abstract AbstractTextFileDao<ObjectDTO, Integer> instant(Path path);


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
//                Arguments.of(list(0)),
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


    protected static StringConverter<ObjectDTO> converter() {
        return new StringConverter<>() {
            @Override
            public ObjectDTO fromString(String str) {
                return ObjectDTO.parse(str);
            }

            @Override
            public String toString(ObjectDTO o) {
                return o.toString();
            }
        };
    }

    protected static class ObjectDTO extends AbstractMap.SimpleEntry<Integer, String> {
        public static ObjectDTO parse(String object) {
            String[] strings = object.split(" \\| ");
            if (strings.length < 2)
                return new ObjectDTO(Integer.valueOf(strings[0]), "");
            else
                return new ObjectDTO(Integer.valueOf(strings[0]), strings[1]);
        }

        public ObjectDTO(Integer key, String value) {
            super(key, value);
        }


        @Override
        public String toString() {
            return getKey() + " | " + getValue();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            ObjectDTO ob = (ObjectDTO) o;
            return getKey().equals(ob.getKey()) && getValue().equals(ob.getValue());
        }
    }

}
