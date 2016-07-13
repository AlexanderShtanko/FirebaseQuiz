package com.alexandershtanko.psychotests.models;

import android.content.Context;

import com.alexandershtanko.psychotests.utils.RxPaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;


/**
 * Created by aleksandr on 17.11.15.
 */
public class Storage {

    private static final String BOOK_TESTS = "TEST";
    private static final String BOOK_RESULTS = "RESULT";
    private static Storage instance;

    private Context context;
    private RxPaper rxPaper;

    private Storage() {
    }

    public static synchronized Storage getInstance() {
        if (instance == null)
            instance = new Storage();
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        rxPaper = RxPaper.getInstance();
        rxPaper.init(context);
    }

    public void destroy() {
        rxPaper.destroy();
        context = null;
        rxPaper = null;
    }


    public void addTest(Test test) {
        rxPaper.write(BOOK_TESTS, test.getInfo().getTestId(), test);
    }

    public Observable<List<Test>> getTestsObservable() {
        Observable<Map<String, RxPaper.PaperObject<Test>>> observable = rxPaper.read(BOOK_TESTS);
        return observable.map(Map::values).map(col -> {
            List<Test> tests = new ArrayList<Test>();
            for (RxPaper.PaperObject<Test> paperObject : col) {
                if (paperObject.getChangesType() != RxPaper.ChangesType.REMOVED) {
                    tests.add(paperObject.getObject());
                }
            }
            return tests;
        });
    }

    public Observable<Test> getTestObservable(String testId) {
        Observable<RxPaper.PaperObject<Test>> observable = rxPaper.read(BOOK_TESTS, testId);
        return observable.filter(po -> po.getChangesType() != RxPaper.ChangesType.REMOVED).map(RxPaper.PaperObject::getObject);
    }

    public void removeTest(String testId) {
        rxPaper.delete(BOOK_TESTS, testId);
    }

    public boolean hasResult(String testId) {
        return rxPaper.hasKey(BOOK_RESULTS, testId);
    }

    public void setResult(String testId, List<Integer> result) {
        rxPaper.write(BOOK_RESULTS, testId, result);
    }

    public List<Integer> getResult(String testId) {
        if (hasResult(testId)) {
            RxPaper.PaperObject<List<Integer>> paperObject = rxPaper.readOnce(BOOK_RESULTS, testId);
            return paperObject.getObject();
        }
        return null;
    }
}