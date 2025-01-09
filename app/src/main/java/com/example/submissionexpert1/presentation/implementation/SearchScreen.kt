package com.example.submissionexpert1.presentation.implementation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.submissionexpert1.presentation.ui.shared.MainSearchBar
import com.example.submissionexpert1.presentation.ui.shared.movie.MovieList
import com.example.submissionexpert1.presentation.viewmodel.SearchEvent
import com.example.submissionexpert1.presentation.viewmodel.SearchViewModel

private const val LOAD_MORE_THRESHOLD = 3

// TODO: benerin infinite scroll nya di view model dan disini
// ! benerin di onSearch, increment page nya
@Composable
fun SearchScreen(
  modifier : Modifier,
  onNavigateDetail : (String) -> Unit,
  onNavigateBack : () -> Unit,
  vm : SearchViewModel = hiltViewModel()
) {


  val uiState by vm.uiState.collectAsState()
  val movieState by vm.movieState.collectAsState()

  LaunchedEffect(uiState) {
    Log.d("SearchScreen", "uiState: $uiState")
  }
  LaunchedEffect(uiState.active) {
    if (! uiState.active) {
      onNavigateBack()
    }
  }

  val movies = if (uiState.isRefreshing) {
    movieState.dataBeforeRefresh?.results ?: emptyList()
  } else {
    movieState.data?.results ?: emptyList()
  }

  val listState = rememberLazyListState()

  val reachedBottom by remember {
    derivedStateOf {
      val layoutInfo = listState.layoutInfo
      val totalItemsCount = layoutInfo.totalItemsCount
      val lastVisibleItemIndex =
        (listState.firstVisibleItemIndex + layoutInfo.visibleItemsInfo.size)

      totalItemsCount > 0 && lastVisibleItemIndex > (totalItemsCount - LOAD_MORE_THRESHOLD)
    }
  }


  LaunchedEffect(reachedBottom) {
    if (reachedBottom && ! uiState.isLoadingMore && ! uiState.isRefreshing) {
      vm.onEvent(SearchEvent.OnSearch)
    }
  }
  LaunchedEffect(
    key1 = uiState.isLoading,
    key2 = uiState.alert != null,
  ) {
    listState.scrollToItem(0)
  }




  Column(
    modifier = modifier
  ) {
    MainSearchBar(
      query = uiState.query,
      onQueryChange = {
        vm.onEvent(SearchEvent.OnQueryChanged(it))
      },
      onSearch = {
        vm.onEvent(SearchEvent.OnSearch)
      },
      active = uiState.active,
      onActiveChange = {
        vm.onEvent(SearchEvent.OnActiveChanged(it))
      },
      enabled = (uiState.isLoading || uiState.isLoadingMore || uiState.isRefreshing),
      allowKeyboard = true,
      modifier = Modifier.fillMaxWidth(),
      callbackTrailingIcon = {
        if (uiState.query.isNotEmpty()) {
          vm.onEvent(SearchEvent.OnQueryChanged(""))
        } else {
          onNavigateBack()
        }
      }

    ) {
      MovieList(
        modifier = modifier,
        movies = movies,
        listState = listState,
        onNavigateDetail = onNavigateDetail,
        alert = uiState.alert,
        isLoading = uiState.isLoading,
        isRefreshing = uiState.isRefreshing,
        isLoadingMore = uiState.isLoadingMore,
        error = uiState.error,
        isLoadingToggleFavorite = uiState.isLoadingToggleFavorite,
        onToggleFavorite = { movieId ->
          vm.onEvent(SearchEvent.OnToggleFavorite(movieId))

        },
        onDismissedAlert = {
          vm.onEvent(SearchEvent.OnDismissedAlert)
        },
        userId = uiState.userId,
        onLoad = {
          vm.onEvent(SearchEvent.OnSearch)
        },
        onRefresh = {
          vm.onEvent(SearchEvent.OnRefresh)
        }

      )

    }


  }
}