package eu.citi_sense.vic.citi_sense.support_classes.map_activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

import eu.citi_sense.vic.citi_sense.MapBaseActivity;
import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.global.Databases.FavoritePlace;
import eu.citi_sense.vic.citi_sense.global.GlobalVariables;
import eu.citi_sense.vic.citi_sense.global.MapVariables;


public class ActionBarFragment extends Fragment {
    public RelativeLayout mFragmentView;
    public TextView mActionBarTitle;
    public ImageView mFavoriteStar;
    private ArrayList<FavoritePlace> mFavoritePlaces;
    private MapBaseActivity mActivity;
    private LatLng location;
    private boolean isFavorite;
    private FavoritePlace currentFavoritePlace;
    private String title;
    private Animation mHideActionMenuAnimation;
    private Animation mShowActionMenuAnimation;
    private ImageView mMenuButton;
    private MenuClickInterface mInterface;
    private boolean mFavoritesStarIsHidden = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FavoritePlace mFavoritePlace = new FavoritePlace();
        mFavoritePlaces = mFavoritePlace.getFavoritePlaces();
        mFragmentView = (RelativeLayout) inflater.inflate(R.layout.map_action_bar_fragment, container, false);
        mActionBarTitle = (TextView) mFragmentView.findViewById(R.id.map_action_bar_title);
        mFavoriteStar = (ImageView) mFragmentView.findViewById(R.id.add_to_favorites);
        mMenuButton = (ImageView) mFragmentView.findViewById(R.id.map_action_bar_menu_button);
        setOnClickListeners();
        createCustomAnimations();
        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mInterface = (MenuClickInterface) activity;
            mActivity = (MapBaseActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MenuClickInterface");
        }
    }
    
    private void setOnClickListeners() {
        mFavoriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    mFavoritePlaces.remove(currentFavoritePlace);
                    currentFavoritePlace.delete();
                    isFavorite = false;
                    mFavoriteStar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_outline));
                } else if (title != null) {
                    if (!title.equals("Dropped pin") || !title.equals("...")) {
                        mFavoriteStar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_full));
                        currentFavoritePlace = new FavoritePlace(
                                location, title, title
                        );
                        currentFavoritePlace.save();
                        mFavoritePlaces.add(currentFavoritePlace);
                        isFavorite = true;
                    }
                }
            }
        });
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.menuClicked();
            }
        });
    }

    public void setTitle(String title, LatLng location) {
        mFavoriteStar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_outline));
        isFavorite = false;
        currentFavoritePlace = null;
        if (title == null) {
            return;
        }
        if (placeExists(title)) {
            isFavorite = true;
        }
        mActionBarTitle.setText(title);
        this.location = location;
        this.title = title;
    }

    private boolean placeExists(String place) {
        for (FavoritePlace favoritePlace : mFavoritePlaces) {
            if(favoritePlace.name.equals(place)) {
                currentFavoritePlace = favoritePlace;
                currentFavoritePlace.setUsed();
                mFavoriteStar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_full));
                return true;
            }
        }
        currentFavoritePlace = null;
        return false;
    }

    public void setTitle(String title) {
        if (title == null) {
            mFavoriteStar.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_outline));
            isFavorite = false;
            currentFavoritePlace = null;
            mActionBarTitle.setText("...");
        } else {
            mActionBarTitle.setText(title);
        }
    }

    private void createCustomAnimations() {
        mHideActionMenuAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFragmentView.getLayoutParams();
                params.topMargin = (int)(-mFragmentView.getHeight()*interpolatedTime);
                mFragmentView.setLayoutParams(params);
            }
        };
        mHideActionMenuAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.mSearchFragment.showSearch();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mHideActionMenuAnimation.setDuration(MapVariables.animationDuration); // in ms

        mShowActionMenuAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFragmentView.getLayoutParams();
                params.topMargin = (int)(mFragmentView.getHeight()*interpolatedTime-mFragmentView.getHeight());
                mFragmentView.setLayoutParams(params);
            }
        };
        mShowActionMenuAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.mSearchFragment.hideSearch();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mShowActionMenuAnimation.setDuration(MapVariables.animationDuration); // in ms
    }

    public void hideMenu(boolean animate) {
        if (((RelativeLayout.LayoutParams) mFragmentView.getLayoutParams()).topMargin == 0) {
            if (animate) {
                mFragmentView.startAnimation(mHideActionMenuAnimation);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFragmentView.getLayoutParams();
                params.topMargin = -10000;
                mFragmentView.setLayoutParams(params);
            }
        }
    }

    public void showMenu() {
        if (((RelativeLayout.LayoutParams) mFragmentView.getLayoutParams()).topMargin != 0) {
            mFragmentView.startAnimation(mShowActionMenuAnimation);
        }
    }

    public String getTitle() {
        return title;
    }

    public LatLng getLocation() {
        return location;
    }

    public interface MenuClickInterface {
        void menuClicked();
    }

    public void setTitleFavorites() {
        if (!mFavoritesStarIsHidden) {
            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setDuration(MapVariables.animationDuration);
            mFavoriteStar.startAnimation(anim);
            mFavoritesStarIsHidden = true;
            mFavoriteStar.setVisibility(View.GONE);
            setTitle(getString(R.string.favorite_places));
        }
    }

    public void setTitleNormal() {
        if (mFavoritesStarIsHidden) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(MapVariables.animationDuration);
            mFavoriteStar.startAnimation(anim);
            mFavoritesStarIsHidden = false;
            mFavoriteStar.setVisibility(View.VISIBLE);
            setTitle(title);
        }
    }
}
