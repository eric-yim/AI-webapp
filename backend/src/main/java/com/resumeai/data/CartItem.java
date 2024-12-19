package com.resumeai.data;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;

@Getter
@Builder(setterPrefix = "with", toBuilder = true)
@ToString
public class CartItem {
    @NonNull
    private final String uidOrderId;

    @NonNull
    private final String quantity;

}
