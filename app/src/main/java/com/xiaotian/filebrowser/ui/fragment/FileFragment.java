package com.xiaotian.filebrowser.ui.fragment;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xiaotian.filebrowser.R;
import com.xiaotian.filebrowser.adapter.file.FileHeaderBinder;
import com.xiaotian.filebrowser.adapter.file.FileItemBinder;
import com.xiaotian.filebrowser.constant.Constant;
import com.xiaotian.filebrowser.event.ViewEvent;
import com.xiaotian.filebrowser.event.ViewEvent.EvenType;
import com.xiaotian.filebrowser.factory.DataType;
import com.xiaotian.filebrowser.factory.FileDataType;
import com.xiaotian.filebrowser.loader.DataLoader;
import com.xiaotian.filebrowser.model.DataModel;
import com.xiaotian.filebrowser.model.domain.FileData;
import com.xiaotian.filebrowser.model.domain.HeaderData;
import com.xiaotian.filebrowser.tool.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import es.dmoral.toasty.Toasty;

/**
 * Created by liwei on 2017/4/28.
 */

public class FileFragment extends ListBaseFragment implements android.app.LoaderManager.LoaderCallbacks<DataModel> {

    private String argStr;
    private Context context;
    private String rootPath;

    public static FileFragment newInstance(String argStr){
        FileFragment fragment = new FileFragment();
        Bundle arg = new Bundle();
        arg.putString(Constant.FRAGMENT_AGR_KEY,argStr);
        fragment.setArguments(arg);
        return fragment;
    }

    private void parseArg(Bundle bundle){
        argStr = bundle.getString(Constant.FRAGMENT_AGR_KEY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        parseArg(this.getArguments());
        adapter.register(FileData.class, new FileItemBinder(context));
        adapter.register(HeaderData.class,new FileHeaderBinder(context));
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            HeaderData headerData = new HeaderData(rootPath);
            items.add(headerData);
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.FILE_DATA_TYPE, FileDataType.init);
            initLoader(bundle);
        }else {
            Toasty.error(context, context.getResources().getString(R.string.sd_notready_tip),
                    Toast.LENGTH_SHORT,true).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ViewEvent event) {
        Bundle bundle = new Bundle();
        switch (event.getType()){
            case gotoFileClickPosition:
                FileData fileData = (FileData) event.getArgs().getSerializable(ViewEvent.Keys.GOTO_PATH);
                bundle.putSerializable(Constant.FILE_DATA_TYPE, FileDataType.gotoPath);
                bundle.putString(Constant.GOTO_PATH,fileData.getPath());
                items.clear();
                items.add(new HeaderData(fileData.getPath()));
                break;
            case backPath:
                HeaderData headerData = (HeaderData) event.getArgs().getSerializable(ViewEvent.Keys.BACK_PATH_HEADER);
                if(rootPath.equals(headerData.getPath())){
                    Toasty.info(context,"当前为SD卡根目录",Toast.LENGTH_SHORT,true).show();
                    return;
                }
                bundle.putSerializable(Constant.FILE_DATA_TYPE, FileDataType.backPath);
                bundle.putString(Constant.BACK_PATH,headerData.getPath());
                String parentPath = FileUtil.getParentPath(headerData.getPath());
                items.clear();
                items.add(new HeaderData(parentPath));
                break;
        }
        initLoader(bundle);
    }

    @Override
    protected void loadData(boolean clear) {
        HeaderData headerData = (HeaderData) items.get(0);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.FILE_DATA_TYPE, FileDataType.gotoPath);
        bundle.putString(Constant.GOTO_PATH,headerData.getPath());
        items.clear();
        items.add(headerData);
        initLoader(bundle);
    }

    @Override
    public void onLoadFinished(Loader<DataModel> loader, DataModel data) {
        items.addAll(data.getDatas());
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        setRefresh(false);
        Toasty.success(context,"refresh current list success",Toast.LENGTH_SHORT,true).show();
    }

    @Override
    public void onLoaderReset(Loader<DataModel> loader) {

    }

    @Override
    public Loader<DataModel> onCreateLoader(int id, Bundle args) {
        args.putSerializable(Constant.DATA_TYPE_KEY,DataType.fileData);
        return new DataLoader(this.getActivity(),args);
    }

    private void initLoader(Bundle bundle){
        getLoaderManager().destroyLoader(DataType.fileData.ordinal());
        getLoaderManager().initLoader(DataType.fileData.ordinal(), bundle, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
