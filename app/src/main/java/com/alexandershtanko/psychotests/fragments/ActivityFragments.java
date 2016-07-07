package com.alexandershtanko.psychotests.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alexandershtanko.psychotests.models.SessionManager;
import com.alexandershtanko.psychotests.models.Test;

import java.util.List;

/**
 * Created by aleksandr on 13.06.16.
 */
public class ActivityFragments {

    public static final String TESTS_FRAGMENT_TAG = TestsFragment.class.getSimpleName();
    public static final String CATEGORIES_FRAGMENT_TAG = CategoriesFragment.class.getSimpleName();
    public static final String TEST_INFO_FRAGMENT_TAG = TestInfoFragment.class.getSimpleName();
    public static final String TEST_FRAGMENT_TAG = TestFragment.class.getSimpleName();
    public static final String TEST_RESULT_FRAGMENT_TAG = TestResultFragment.class.getSimpleName();
    private static final String TAG = ActivityFragments.class.getSimpleName();
    private static ActivityFragments instance;
    private FragmentManager fragmentManager;
    private int containerId;

    public static synchronized ActivityFragments getInstance() {
        if (instance == null)
            instance = new ActivityFragments();
        return instance;
    }

    public void init(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }


    public void openTests() {
        replaceFragment(TestsFragment.getInstance(), TESTS_FRAGMENT_TAG, true);
    }

    public void openTests(String category) {
        replaceFragment(TestsFragment.getInstance(category), TESTS_FRAGMENT_TAG, true);
    }

    public void openCategories() {
        replaceFragment(CategoriesFragment.getInstance(), CATEGORIES_FRAGMENT_TAG, true);
    }

    public void openPassedTests() {
        replaceFragment(TestsFragment.getInstanceForPassedTests(), TESTS_FRAGMENT_TAG, true);
    }

    public void openTestInfo(Test test) {
        SessionManager.getInstance().setTest(test);
        replaceFragment(TestInfoFragment.getInstance(), TEST_INFO_FRAGMENT_TAG, true);
    }

    public void openTest() {
        replaceFragment(TestFragment.getInstance(), TEST_FRAGMENT_TAG, true);
    }

    public void openTestResult(List<Integer> result) {
        SessionManager.getInstance().setResult(result);
        replaceFragment(TestResultFragment.getInstance(), TEST_RESULT_FRAGMENT_TAG, true);
    }

    private void replaceFragment(Fragment fragment, String tag, boolean toBackStack) {
        Fragment old = fragmentManager.findFragmentByTag(tag);

        if (old != null)
            fragmentManager.beginTransaction().remove(old).commit();

        FragmentTransaction transaction = fragmentManager.beginTransaction().replace(containerId, fragment, tag);

        if (toBackStack) {
            fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.addToBackStack(tag);
        }

        transaction.commit();


    }

}
