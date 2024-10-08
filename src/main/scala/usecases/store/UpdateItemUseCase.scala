package usecases.store

import com.google.inject.{Inject, Singleton}
import contract.callback.store.ItemCallback
import contract.callback.user.{SessionCallback, UserCallback, UserPermissionCallback}
import contract.service.store.{AddItemService, UpdateItemService}
import domain.user.UserPermission
import domain.store.Item

import scala.Option.option2Iterable
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateItemUseCase @Inject()(itemCallback: ItemCallback,userPermissionCallback: UserPermissionCallback) extends UpdateItemService {


  override def call(request: UpdateItemService.Request)(implicit ec: ExecutionContext): Future[Unit] = for {


    // Step 2: Check if the user has the permission to update a product
    hasPermission <- userPermissionCallback.get(request.userID, UserPermission.Permission.UPDATE_ITEM)
    _ = hasPermission getOrElse {
      Future.failed(new Exception("User does not have permission to add products"))
    }

    // Step 3: Check if the product name exists
    itemExists <- itemCallback.getByName(request.name)
    _= if (itemExists.nonEmpty){
      Future.failed(new Exception(s"Product with name '${request.name}' is not exist exists"))
    }

    // Step 4: Update product
    itemId <- itemCallback.update(request.name,  request.stock, request.price, request.description)
//    item <-itemCallback.get(itemId)


  } yield ()
}
