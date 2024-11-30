package com.example.mybatplusdemo;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
public class AITest {

    @Test
    public void testLamda() {
        Runnable r1 = () -> System.out.println("Hello World 1");
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello World 2");
            }
        };
        process(r1);
        process(r2);
        process(() -> System.out.println("Hello World 3"));

    }
    public static void process(Runnable r) {
        r.run();
    }

    public static int strLength(String str) {
        return str.length();
    }

    @Test
    public void openNlp() throws IOException {
        InputStream modelIn = getClass().getResourceAsStream("/opennlp-models/en-ner-person.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
        NameFinderME nameFinder = new NameFinderME(model);
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String text = "John Doe is a great person.Mike is handsome Boy!";
        String[] tokens = tokenizer.tokenize(text);
        Span[] spans = nameFinder.find(tokens);
        StringBuilder result = new StringBuilder();
        for (Span span : spans) {
            result.append(span.toString()).append(" ");
        }
        System.out.println(result.toString());
//        return result.toString();
    }

    @Test
    public void test215() {
        float[] inputData = {1.0f,2.0f,3.0f};
        SavedModelBundle model = SavedModelBundle.load(null, "serve");


        // 创建输入张量
        Tensor<Float> inputTensor = Tensor.create(inputData, Float.class);

        // 执行推理
        Tensor<?> outputTensor = model.session().runner()
                .fetch("output_tensor_name") // 替换为你的输出张量名称
                .feed("input_tensor_name", inputTensor) // 替换为你的输入张量名称
                .run().get(0);

        // 处理输出
        float[] outputData = new float[(int) outputTensor.shape()[0]];
        outputTensor.copyTo(outputData);
        System.out.println(outputData);
    }
}
