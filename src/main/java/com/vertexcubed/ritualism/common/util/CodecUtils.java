package com.vertexcubed.ritualism.common.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.List;
import java.util.function.Function;

public class CodecUtils {


    /**
     * Codec that represents either a single element or a list of elements.
     * @param codec Input codec
     */
    public static <T> Codec<List<T>> listOrElementCodec(final Codec<T> codec) {
        return Codec.either(codec, codec.listOf()).xmap(
                codecEither -> codecEither.map(ImmutableList::of, Function.identity()),
                list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list)
        );
    }
}
