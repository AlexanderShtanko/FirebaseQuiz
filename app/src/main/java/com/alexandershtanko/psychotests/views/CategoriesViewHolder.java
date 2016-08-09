package com.alexandershtanko.psychotests.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.alexandershtanko.psychotests.R;
import com.alexandershtanko.psychotests.fragments.ActivityFragments;
import com.alexandershtanko.psychotests.models.Category;
import com.alexandershtanko.psychotests.viewmodels.CategoriesViewModel;
import com.alexandershtanko.psychotests.views.adapters.CategoriesAdapter;
import com.alexandershtanko.psychotests.vvm.AbstractViewBinder;
import com.alexandershtanko.psychotests.vvm.AbstractViewHolder;

import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by aleksandr on 12.06.16.
 */
public class CategoriesViewHolder extends AbstractViewHolder {
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.layout_internet_connection)
    View internetConnectionLayout;

    CategoriesAdapter adapter;

    public CategoriesViewHolder(Context context, int layoutRes) {
        super(context, layoutRes);
        adapter = new CategoriesAdapter();
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
    }

    public void add(Category category) {
        adapter.add(category);
    }

    public void populate(List<Category> categories) {
        adapter.populate(categories);
    }

    private void populateEmpty(Boolean isEmpty) {
        internetConnectionLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

    }

    public static class ViewBinder extends AbstractViewBinder<CategoriesViewHolder, CategoriesViewModel> {
        public ViewBinder(CategoriesViewHolder viewHolder, CategoriesViewModel viewModel) {
            super(viewHolder, viewModel);

        }

        @Override
        protected void onBind(CompositeSubscription s) {
            s.add(Observable.create((Observable.OnSubscribe<List<Category>>) subscriber -> subscriber.onNext(viewModel.getCategories())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(viewHolder::populate));


            s.add(viewModel.getCategoryObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(viewHolder::add));
            s.add(viewModel.getErrorObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::showError));
            s.add(Observable.create((Observable.OnSubscribe<String>) subscriber -> viewHolder.adapter.setOnItemClickListener(subscriber::onNext))
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(this::selectCategory));
            s.add(viewModel.getEmptyObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(viewHolder::populateEmpty));

        }

        private void selectCategory(String category) {
            ActivityFragments.getInstance().openTests(category);
        }

        public void showError(Throwable throwable) {
            Toast.makeText(viewHolder.getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
