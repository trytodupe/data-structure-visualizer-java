package com.trytodupe.test;

import com.google.gson.JsonObject;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.serialization.GsonProvider;
import com.trytodupe.serialization.ISerializable;

import java.util.ArrayList;
import java.util.List;

public class OperationTestRunner {

	public static void runTestSuite (Class<?> clazz, List<? extends UserOperation<?>> operations) {

		List<UserOperation<?>> deserializedOperations = new ArrayList<>();

		System.out.println("=== Running test suite for " + clazz.getSimpleName() + " ===\n");

		// forward testing
		for (UserOperation<?> op : operations) {
			System.out.println("Executing: " + op.getDescription());

			// serialize
			JsonObject json = op.toJson(GsonProvider.get());
			System.out.println("Serialized JSON: " + json);

			// deserialize
			ISerializable deserialized = ISerializable.fromJson(GsonProvider.get(), json);
			System.out.println("Deserialized type: " + deserialized.getClass().getSimpleName());


			// execute
			if (deserialized instanceof UserOperation<?>) {
				deserializedOperations.add((UserOperation<?>) deserialized);
				((UserOperation<?>) deserialized).execute();
			}

			System.out.println();
		}

		// backward testing
		for (int i = deserializedOperations.size() - 1; i >= 0; i--) {
			UserOperation<?> op = deserializedOperations.get(i);
			System.out.println("Undoing: " + op.getDescription());
			op.undo();
			System.out.println();
		}

		System.out.println("=== Test suite finished ===\n");
	}
}
