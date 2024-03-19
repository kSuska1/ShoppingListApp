package com.example.shoppinglistapp

import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item-table")
data class ShoppingItem(@PrimaryKey(autoGenerate = true)
                        val id: Int,
                        @ColumnInfo(name = "product-name")
                        var name: String,
                        @ColumnInfo(name = "product-amt")
                        var amount: Int,
                        var isEditing: Boolean = false
)

@Composable
fun ShoppingListApp()
{
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("")}
    var itemAmount by remember { mutableStateOf("")}

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Your shopping list",
            style = TextStyle(color = Color.DarkGray,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
            )
        Button(
            onClick = { showDialog = true},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            Text(text = "Add item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            items(sItems){
                item ->
                if(item.isEditing){
                    ShoppingItemEditor(item = item,
                        onEditComplete = {
                        editedName, editedAmount ->
                        sItems = sItems.map { it.copy(isEditing = false)}
                        val editedItem = sItems.find { it.id == item.id }
                        editedItem?.let {
                            it.name = editedName
                            it.amount = editedAmount
                        }
                    })
                }else{
                    ShoppingListItem(item = item,
                        onEditClick = {
                        sItems = sItems.map{ it.copy(isEditing = it.id ==item.id)}
                    },
                        onDeleteClick = {
                        sItems = sItems - item
                    })
                }
            }
        }
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween)
                            {
                                Button(onClick = {
                                    if(itemName.isNotBlank()){
                                        val newItem = ShoppingItem(
                                            id = sItems.size+1,
                                            name = itemName,
                                            amount = itemAmount.toInt()
                                        )
                                        sItems = sItems + newItem
                                        showDialog = false
                                        itemName = ""
                                        itemAmount = ""
                                    }
                                })
                                {
                                    Text(text = "Add")
                                }
                                Button(onClick = { showDialog = false })
                                {
                                    Text(text = "Cancel")
                                }
                            }
            },
            title = { Text(text = "Add shopping item")},
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {itemName = it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        placeholder = { Text(text = "Enter the item",
                            style = TextStyle(color = Color.LightGray)
                        )}
                        )

                    OutlinedTextField(
                        value = itemAmount,
                        onValueChange = {itemAmount = it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        placeholder = { Text(text = "Enter quantity",
                            style = TextStyle(color = Color.LightGray)
                            )}
                    )
                }
            })

        }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit){
    var editedName by remember { mutableStateOf(item.name) }
    var editedAmount by remember { mutableStateOf(item.amount.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly)
    {
        Column {
            BasicTextField(value = editedName,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
                )

            BasicTextField(value = editedAmount,
                onValueChange = {editedAmount = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName, editedAmount.toIntOrNull() ?: 1)

        }) {
            Text(text = "Save")
        }

    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,

){
    var isChecked by remember{ mutableStateOf(false) }
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color.Gray),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Checkbox(checked = isChecked, onCheckedChange = {isChecked = it},
            modifier = Modifier.align(Alignment.CenterVertically)
            )
        Text(text = item.name,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterVertically))
        Text(text = "Amt: ${item.amount}",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterVertically))

        Row (modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }

}
