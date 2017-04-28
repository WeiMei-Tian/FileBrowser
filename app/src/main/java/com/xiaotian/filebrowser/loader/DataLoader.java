package com.xiaotian.filebrowser.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.xiaotian.filebrowser.constant.Constant;
import com.xiaotian.filebrowser.factory.DataFactory;
import com.xiaotian.filebrowser.factory.DataType;
import com.xiaotian.filebrowser.model.DataModel;

/**
 * Created by admin on 2016/11/22.
 */
public class DataLoader extends AsyncTaskLoader<DataModel> {


    private DataModel dataModel;

    public DataLoader(Context context, Bundle bundle) {
        super(context);
        DataType dataType = (DataType) bundle.getSerializable(Constant.DATA_TYPE_KEY);
        dataModel = DataFactory.getInstance().createDataModel(context,dataType,bundle);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public DataModel loadInBackground() {
        dataModel.loadData();
        return dataModel;
    }
}
